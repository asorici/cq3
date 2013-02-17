import sys, os, string, time, shutil
from subprocess import call, Popen, PIPE

ROOT = os.path.abspath(os.getcwd() + "/../")
SCRIPTS = ROOT + "/scripts"
SUBSFILE = SCRIPTS + "/submissions_file"
SUBS = ROOT + "/submissions"
FRAMEWORK = ROOT + "/cqframework"
JOBS = ROOT + "/jobs"

RESULTS_DUMP_FILE_PREFIX = "result_dump_one_on_one"
RESULTS_DUMP_FILE = ROOT + "/result_dump.txt"

# maps
maplist = ['map_cq3_v1.cqm', 'map_cq3_v2.cqm', 'map_cq3_v3.cqm']

# secrets - the mapping to the player IDs must match the order in secrets.txt !!!!!!!
secrets = {1 : 1, 
           2 : 2}

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
    
    def serialize_results(self):
        results_dict = {
                        'team_id' : self.team_id,
                        'team_name': self.team_name,
                        'finish_position': self.finish_position,
                        'points': self.points
                        }
        
        for stat in scoring_criteria:
            results_dict[stat] = getattr(self, stat, None)
            
        return results_dict

       
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
    
    for competitorstring in competitorDataStrings:
        data = competitorstring.split()
        datadict = {'teamname': data[0], 'teamid': int(data[1]), 'teamjar': data[2], 'teamclass': data[3]}
        competitorData.append(datadict)
        
    
    ''' generate match structure - a team will play against every other team except itself '''
    matchid = 0
    for mapp in maplist:
        for i in range(len(competitorData)):
            for j in range(len(competitorData)):
                if i != j:
                    gamestruct[matchid] = Game(2, mapp)
                    
                    # team i will be player 1
                    teamI = TeamDataInGame(competitorData[i]['teamname'], 
                                           competitorData[i]['teamid'], 
                                           competitorData[i]['teamjar'], 
                                           competitorData[i]['teamclass'], 
                                           secrets[1])
                    gamestruct[matchid].add_team_data(1, teamI)
                    
                    
                    # team j will be player 2
                    teamJ = TeamDataInGame(competitorData[j]['teamname'], 
                                           competitorData[j]['teamid'], 
                                           competitorData[j]['teamjar'], 
                                           competitorData[j]['teamclass'], 
                                           secrets[2])
                    gamestruct[matchid].add_team_data(2, teamJ)
                    
                    matchid += 1
    
    
                
    ''' create physical dir structure from gamestructure '''
    ## cleanup any existing jobs
    if os.path.isdir(JOBS):
        shutil.rmtree(JOBS)
    
    ## create new directory structure
    for matchid, game in gamestruct.items():
            
            os.makedirs(JOBS + "/" + str(matchid) + "/s")
            os.makedirs(JOBS + "/" + str(matchid) + "/a1")
            os.makedirs(JOBS + "/" + str(matchid) + "/a2")
            
            # create server directory structure 
            current_server_dir = JOBS + "/" + str(matchid) + "/s"
            os.symlink(FRAMEWORK + "/cqserver/maps/", current_server_dir + "/maps")
            os.symlink(FRAMEWORK + "/cqserver/lib/", current_server_dir + "/lib")
            #os.symlink(FRAMEWORK + "/cqserver/images/", current_server_dir + "/images")
            
            # copy the correct GamePolicy file
            policyFile = FRAMEWORK + "/cqserver/" + "GamePolicy_" + game.map_name.split(".")[0] + ".xml"
            os.symlink(policyFile, current_server_dir + "/GamePolicy.xml")
            os.symlink(FRAMEWORK + "/cqserver/logging.properties", current_server_dir + "/logging.properties")
            os.symlink(FRAMEWORK + "/cqserver/cqserver.jar", current_server_dir + "/cqserver.jar")
            
            # copy appropriate secrets file
            os.symlink(FRAMEWORK + "/cqserver/secrets_1_on_1.txt", current_server_dir + "/secrets.txt")
            os.symlink(SCRIPTS + "/job_server", current_server_dir + "/job_server")
            
            f = open(current_server_dir + "/mapdata", 'a')
            print >>f, game.map_name
            f.close()
            
            f = open(current_server_dir + "/config", 'a')
            print >>f, 'set SERVERNAME = "' + game.server_name + '"'
            print >>f, 'set SERVERHOST = "' + game.server_host + '"'
            print >>f, 'set SERVERPORT = "' + game.server_port + '"'
            f.close()
            
            # create agent1 directory structure
            current_client_dir = JOBS + "/" + str(matchid) + "/a1"
            #os.symlink(FRAMEWORK + "/cqclient/lib/", current_client_dir + "/lib")
            os.symlink(FRAMEWORK + "/cqclient/logging.properties", current_client_dir + "/logging.properties")
            os.symlink(FRAMEWORK + "/cqclient/cq.policy", current_client_dir + "/cq.policy")
            os.symlink(SUBS + "/" + game.teams[1].team_jar, current_client_dir + "/playerdist.jar")
            os.symlink(SCRIPTS + "/job_agent", current_client_dir + "/job_agent")
            
            f = open(current_client_dir + "/config", 'a')
            print >>f, 'set SERVERNAME = "' + game.server_name + '"'
            print >>f, 'set SERVERHOST = "' + game.server_host + '"'
            print >>f, 'set SERVERPORT = "' + game.server_port + '"'
            print >>f, 'set MAINCLASS = "' + game.teams[1].team_class + '"'
            print >>f, 'set JAR = "' + game.teams[1].team_jar + '"'
            print >>f, 'set SECRETID = "' + str(game.teams[1].team_secret) + '"'
            f.close()
            
            
            # create agent2 directory structure
            current_client_dir = JOBS + "/" + str(matchid) + "/a2"
            #os.symlink(FRAMEWORK + "/cqclient/lib/", current_client_dir + "/lib")
            os.symlink(FRAMEWORK + "/cqclient/logging.properties", current_client_dir + "/logging.properties")
            os.symlink(FRAMEWORK + "/cqclient/cq.policy", current_client_dir + "/cq.policy")
            os.symlink(SUBS + "/" + game.teams[2].team_jar, current_client_dir + "/playerdist.jar")
            os.symlink(SCRIPTS + "/job_agent", current_client_dir + "/job_agent")
            
            f = open(current_client_dir + "/config", 'a')
            print >>f, 'set SERVERNAME = "' + game.server_name + '"'
            print >>f, 'set SERVERHOST = "' + game.server_host + '"'
            print >>f, 'set SERVERPORT = "' + game.server_port + '"'
            print >>f, 'set MAINCLASS = "' + game.teams[2].team_class + '"'
            print >>f, 'set JAR = "' + game.teams[2].team_jar + '"'
            print >>f, 'set SECRETID = "' + str(game.teams[2].team_secret) + '"'
            f.close()
            
    ''' begin running of matches '''
    try:
        for matchid, game in gamestruct.items():
                print "Running match " + game.teams[1].team_name + " vs " + game.teams[2].team_name \
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
                    
                    print "#### Starting agent 1 " + game.teams[1].team_name + " ####"
                    current_client_dir = JOBS + "/" + str(matchid) + "/a1"
                    os.chdir(current_client_dir)
                    cmd  = ["./job_agent"]
                    clientprocess1 = Popen(cmd)
                    
                    print "#### Starting agent 2 " + game.teams[2].team_name + " ####"
                    current_client_dir = JOBS + "/" + str(matchid) + "/a2"
                    os.chdir(current_client_dir)
                    cmd  = ["./job_agent"]
                    clientprocess2 = Popen(cmd)
                    
                    # wait for server to stop
                    server_returncode = serverprocess.wait()
                    clientprocess1.kill()
                    clientprocess2.kill()
                    
                    serverprocess = None
                    clientprocess1 = None
                    clientprocess2 = None
                    
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
        
        ## we know there are only two lines - one for each player
        winner_game_results = filecontents[0].split(",")
        loser_game_results = filecontents[1].split(",")
        
        ## process winner
        winner_player_id = int(winner_game_results[0])
        winner_results = {'finish_position' : 1}
        if float(winner_game_results[1]) == float(loser_game_results[1]):
            winner_results['points'] =  1
        else:
            winner_results['points'] =  2
        
        for idx in range(len(scoring_criteria)):
            scoring_item = scoring_criteria[idx]
            winner_results[scoring_item] = num(winner_game_results[idx + 1])
        
        game.set_team_game_results(winner_player_id, winner_results)
        
        ## process loser
        loser_player_id = int(loser_game_results[0])
        loser_results = {'finish_position' : 2}
        if float(winner_game_results[1]) == float(loser_game_results[1]):
            loser_results['points'] = 1
        else:
            loser_results['points'] = 0
        
        for idx in range(len(scoring_criteria)):
            scoring_item = scoring_criteria[idx]
            loser_results[scoring_item] = num(loser_game_results[idx + 1])
        
        game.set_team_game_results(loser_player_id, loser_results)
        
    except Exception, ex:
        print "Score collect failed for " + game.teams[1].team_name + " vs " + game.teams[2].team_name + ". Reason: ", ex


def generate_team_stats(results_dump_file):
    """
    Generates team match statistics and organizes them by map. 
    Thus the dictionary mapping will look like the following:
    
    map:
        team_id:
            team_name: name
            total_points: total_points
            games:    // the list of games played by the currently analyzed team
                [{ stat1: stat1,
                   stat2: stat2,
                   ...
                   opponent_data:[    // A list of dictionaries for each of the opponents in this game. 
                       {},            // The dictionaries contain the same statistics as for the currently
                       {}             // analyzed team.
                   ] 
                 },
                 
                 {...}
                ]
    """
    
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
        print "Usage: python create_games_one_on_one.py <submissions_filename>"
        exit(1)
    
    main(submissions_filename)
