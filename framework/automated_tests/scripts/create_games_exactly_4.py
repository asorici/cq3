import sys, os, string, time, shutil, random
from subprocess import call, Popen, PIPE

ROOT = os.path.abspath(os.getcwd() + "/../")
SCRIPTS = ROOT + "/scripts"
SUBSFILE = SCRIPTS + "/submissions_file"
SUBS = ROOT + "/submissions"
FRAMEWORK = ROOT + "/cqframework"
JOBS = ROOT + "/jobs"

RESULTS_DUMP_FILE_PREFIX = "result_dump_exactly_4"
RESULTS_DUMP_FILE = ROOT + "/result_dump.txt"

# maps
maplist = ['map_cq3_v1.cqm', 'map_cq3_v2.cqm', 'map_cq3_v3.cqm']

# secrets - the mapping to the player IDs must match the order in secrets.txt !!!!!!!
secrets = {1 : 1, 
           2 : 2,
           3 : 3,
           4 : 4}

# scoring criteria
scoring_criteria = ['total_score', 'kills', 'retaliation_kills', 'dead_units', 'placed_towers', 
                    'successful_traps', 'placed_traps', 'killing_sprees', 'first_blood']

# competitor data and game structure
competitorData = []
gamestruct = {}
team_stats_by_map = {}

class Game(object):
    server_name = "CraftingQuest"
    server_host = "localhost"
    server_port = "1198"
    
    def __init__(self, num_players, map_name):
        self.map_name = map_name
        self.num_players = num_players
        self.teams = {}
    
    def add_team_data(self, player_id, team_data):
        self.teams[player_id] = team_data
        
    def set_team_game_results(self, player_id, team_result):
        self.teams[player_id].set_results(**team_result)
    

class TeamDataInGame(object):
    def __init__(self, team_name, team_id, team_jar, team_class, team_secret):
        self.team_name = team_name
        self.team_id = team_id
        self.team_jar = team_jar
        self.team_class = team_class
        self.team_secret = team_secret
        
    def set_results(self, finish_position = 1, points = 0, total_score = 0, kills = 0,
                    retaliation_kills = 0, dead_units = 0, placed_towers = 0,
                    successful_traps = 0, placed_traps = 0, killing_sprees = 0, first_blood = 0):
        self.finish_position = finish_position
        self.points = points
        self.total_score = total_score
        self.kills = kills
        self.retaliation_kills = retaliation_kills
        self.dead_units = dead_units
        self.placed_towers = placed_towers
        self.successful_traps = successful_traps
        self.placed_traps = placed_traps
        self.killing_sprees = killing_sprees
        self.first_blood = first_blood
       
    def __repr__(self):
        return  self.team_name + "<" + str(self.team_secret) + ">"   
    
    def __unicode__(self):
        return  self.team_name + "<" + str(self.team_secret) + ">"


def num(s):
    try:
        return int(s)
    except ValueError:
        return float(s)


def main(submissions_filename):
    SUBSFILE = SCRIPTS + "/" + submissions_filename
    RESULTS_DUMP_FILE = ROOT + "/" + RESULTS_DUMP_FILE_PREFIX + "_" + submissions_filename + ".txt"
    
    ''' read team data and generate game structure '''
    subf = open(SUBSFILE, 'r')
    competitorDataStrings  = map(lambda x: string.strip(x), subf.readlines())
    
    ''' if there are more than 4 teams amongst the competitors throw an error '''
    if len(competitorDataStrings) != 4:
        raise ValueError("Wrong `submissions' file. Check that there are exactly 4 solutions listed.")
    
    for competitorstring in competitorDataStrings:
        data = competitorstring.split()
        datadict = {'teamname': data[0], 'teamid': data[1], 'teamjar': data[2], 'teamclass': data[3]}
        competitorData.append(datadict)
    
    
    ''' generate match structure - a group of 4 will play 5 matches starting at random map positions each time '''
    rand_player_ids = [1,2,3,4]
    matchid = 0

    ''' cycle through the maps 5 times '''
    for mIdx in range(5):
        mapp = maplist[mIdx % len(maplist)]
        gamestruct[matchid] = Game(4, mapp)
        
        ## generate random permutation of player ids 1 - 4
        random.shuffle(rand_player_ids)
        
        for i in range(4):
            player_id = rand_player_ids[i]
            team = TeamDataInGame(competitorData[i]['teamname'], 
                               competitorData[i]['teamid'], 
                               competitorData[i]['teamjar'], 
                               competitorData[i]['teamclass'], 
                               secrets[player_id])
            
            gamestruct[matchid].add_team_data(player_id, team)
        
        matchid += 1
    
    
                
    ''' create physical dir structure from gamestructure '''
    ## cleanup any existing jobs
    if os.path.isdir(JOBS):
        shutil.rmtree(JOBS)
    
    ## create new directory structure
    for matchid, game in gamestruct.items():        
        os.makedirs(JOBS + "/" + str(matchid) + "/s")
        
        for player_id, team_data in game.teams.items():
            os.makedirs(JOBS + "/" + str(matchid) + "/a" + str(player_id))
        
        # create server directory structure 
        current_server_dir = JOBS + "/" + str(matchid) + "/s"
        os.symlink(FRAMEWORK + "/cqserver/maps/", current_server_dir + "/maps")
        os.symlink(FRAMEWORK + "/cqserver/lib/", current_server_dir + "/lib")
        os.symlink(FRAMEWORK + "/cqserver/images/", current_server_dir + "/images")
        
        # copy the correct GamePolicy file
        policyFile = FRAMEWORK + "/cqserver/" + "GamePolicy_" + game.map_name.split(".")[0] + ".xml"
        os.symlink(policyFile, current_server_dir + "/GamePolicy.xml")
        os.symlink(FRAMEWORK + "/cqserver/logging.properties", current_server_dir + "/logging.properties")
        os.symlink(FRAMEWORK + "/cqserver/cqserver.jar", current_server_dir + "/cqserver.jar")
        
        # copy appropriate secrets file
        os.symlink(FRAMEWORK + "/cqserver/secrets_exactly_4.txt", current_server_dir + "/secrets.txt")
        os.symlink(SCRIPTS + "/job_server", current_server_dir + "/job_server")
        
        f = open(current_server_dir + "/mapdata", 'a')
        print >>f, game.map_name
        f.close()
        
        f = open(current_server_dir + "/config", 'a')
        print >>f, 'set SERVERNAME = "' + game.server_name + '"'
        print >>f, 'set SERVERHOST = "' + game.server_host + '"'
        print >>f, 'set SERVERPORT = "' + game.server_port + '"'
        f.close()
        
        # create client agent directory structure
        
        for player_id, team_data in game.teams.items():
            current_client_dir = JOBS + "/" + str(matchid) + "/a" + str(player_id)
            #os.symlink(FRAMEWORK + "/cqclient/lib/", current_client_dir + "/lib")
            os.symlink(FRAMEWORK + "/cqclient/logging.properties", current_client_dir + "/logging.properties")
            os.symlink(FRAMEWORK + "/cqclient/cq.policy", current_client_dir + "/cq.policy")
            os.symlink(SUBS + "/" + team_data.team_jar, current_client_dir + "/playerdist.jar")
            os.symlink(SCRIPTS + "/job_agent", current_client_dir + "/job_agent")
            
            f = open(current_client_dir + "/config", 'a')
            print >>f, 'set SERVERNAME = "' + game.server_name + '"'
            print >>f, 'set SERVERHOST = "' + game.server_host + '"'
            print >>f, 'set SERVERPORT = "' + game.server_port + '"'
            print >>f, 'set MAINCLASS = "' + team_data.team_class + '"'
            print >>f, 'set JAR = "' + team_data.team_jar + '"'
            print >>f, 'set SECRETID = "' + str(team_data.team_secret) + '"'
            f.close()
            
            
    ''' begin running of matches '''
    try:
        for matchid, game in gamestruct.items():
                print "Running match " + "(" + " ".join([td.team_name for td in game.teams.values()]) + ")"\
                    + " on map " + game.map_name
                
                serverprocess = None
                clientprocess1 = None
                clientprocess2 = None
                
                try:
                    print "#### Starting server ####"
                    current_server_dir = JOBS + "/" + str(matchid) + "/s"
                    print current_server_dir
                    os.chdir(current_server_dir)
                    cmd  = ["./job_server"]
                    serverprocess = Popen(cmd)
                    time.sleep(2)
                    
                    clientprocess_list = []
                    for player_id, team_data in game.teams.items():
                        print "#### Starting agent " + str(player_id) + " " + team_data.team_name + " ####"
                        current_client_dir = JOBS + "/" + str(matchid) + "/a" + str(player_id)
                        os.chdir(current_client_dir)
                        cmd  = ["./job_agent"]
                        
                        clientprocess = Popen(cmd)
                        clientprocess_list.append(clientprocess)
                        
                    
                    # wait for server to stop
                    server_returncode = serverprocess.wait()
                    for clientprocess in clientprocess_list:
                        clientprocess.kill()
                        clientprocess = None
                    
                    serverprocess = None
                    clientprocess_list = []
                    
                    print "Server job exited with returncode: ", server_returncode
                    print ""
                
                    collect_score(current_server_dir, game)
                    
                except Exception, e:
                    print "Game " + game.teams[1].team_name + " vs " + game.teams[2].team_name + " on map " + game.map_name + " failed. Reason", e
                    try:
                        if serverprocess:
                            serverprocess.kill()
                        if clientprocess1:
                            clientprocess1.kill()
                        if clientprocess2:
                            clientprocess2.kill()
                    except:
                        pass
        
        print "#### ALL GAMES SUCCESSFULLY PLAYED ####"
        
    except Exception, ex:
        print "Game run process stopped because of: ", ex
        
    
    print "#### Generating team stats and dumping json output ####"
    generate_team_stats(RESULTS_DUMP_FILE)


def collect_score(current_server_dir, game):
    os.chdir(current_server_dir)
    
    try:
        f = open("winner.txt", "r")
        filecontents = map(lambda x: string.strip(x), f.readlines())
        
        ## the lines are sorted such that the player ids are in order of their result in the game
        num_lines = len(filecontents)
        player_points = []
        player_position = []
        
        for i in range(num_lines):
            content_line = filecontents[i]
            results = content_line.split(",")
            player_id = int(results[0])
            player_score = float(results[1])
            
            player_results = {'finish_position' : (i + 1),
                              'points': game.num_players - (i + 1)}
            
            if i > 0:
                prev_player_score = float(filecontents[i - 1].split(",")[1])
                if player_score == prev_player_score:
                    player_results['points'] = player_points[i - 1]
                    player_results['finish_position'] = player_position[i - 1]
            
            player_points.append(player_results['points'])
            player_position.append(player_results['finish_position'])
            
            # other scoring criteria
            for idx in range(len(scoring_criteria)):
                scoring_item = scoring_criteria[idx]
                player_results[scoring_item] = num(results[idx + 1])
            
            game.set_team_game_results(player_id, player_results)
    except Exception, ex:
        print "Score collect failed for match: ", [game.teams[i].team_name for i in range(1, game.num_players + 1)], ". Reason: ", ex


def generate_team_stats(results_dump_file):
    ## generate team stats by map
    
    for map_name in maplist:
        team_stats_by_map[map_name] = {}
    
    for matchid, game in gamestruct.items():
        for player_id in range(1, (game.num_players + 1)):
            team_id = game.teams[player_id].team_id
            opponent_data = map(lambda tuple: dict(tuple[1].serialize_results(), player_id = tuple[0]), 
                                filter(lambda pID_team_tuple: pID_team_tuple[1].team_id != team_id, game.teams.items()))
            
            match_details = {   'oponent_data': opponent_data,
                                'player_id': player_id,
                                'finish_position': game.teams[player_id].finish_position,
                                'points': game.teams[player_id].points,
                                'total_score': game.teams[player_id].total_score,
                                'kills': game.teams[player_id].kills,
                                'retaliation_kills': game.teams[player_id].retaliation_kills,
                                'dead_units': game.teams[player_id].dead_units,
                                'placed_towers': game.teams[player_id].placed_towers,
                                'successful_traps': game.teams[player_id].successful_traps,
                                'placed_traps': game.teams[player_id].placed_traps,
                                'killing_sprees': game.teams[player_id].killing_sprees,
                                'first_blood': game.teams[player_id].first_blood
                            }
            
            if team_id in team_stats_by_map[game.map_name]:
                team_stats_by_map[game.map_name][team_id]['total_points'] += game.teams[player_id].points
                team_stats_by_map[game.map_name][team_id]['games'].append(match_details)
            else:
                team_games = []
                team_games.append(match_details)
                
                team_stats_by_map[game.map_name][team_id] = {'team_name': game.teams[player_id].team_name,
                                                            'total_points': game.teams[player_id].points,
                                                            'games': team_games
                                                            }
    
    import simplejson
    f = open(results_dump_file, 'w')
    print >>f, simplejson.dumps(team_stats_by_map, indent=1)
    f.close()

    
if __name__ == '__main__':
    try:
        submissions_filename = sys.argv[1]
    except:
        print "Usage: python create_games_exactly_4.py <submissions_filename>"
        exit(1)
    
    main(submissions_filename)
