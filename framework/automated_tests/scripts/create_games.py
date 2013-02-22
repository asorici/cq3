import sys, os, string, time, shutil, random, itertools
from subprocess import call, Popen, PIPE

ROOT = os.path.abspath(os.getcwd() + "/../")
SCRIPTS = ROOT + "/scripts"
SUBSFILE = SCRIPTS + "/submissions_file"
SUBS = ROOT + "/submissions"
FRAMEWORK = ROOT + "/cqframework"
JOBS = ROOT + "/jobs"

RESULTS_DUMP_FILE_PREFIX = "result_dump_general"
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


def main(submissions_filename, num_players_on_map):
    SUBSFILE = SCRIPTS + "/" + submissions_filename
    RESULTS_DUMP_FILE = ROOT + "/" + RESULTS_DUMP_FILE_PREFIX + "_" + submissions_filename + ".txt"
    
    if num_players_on_map > 4:
        raise ValueError("Maximum number (4) of players exceeded.")
    
    ''' read team data and generate game structure '''
    subf = open(SUBSFILE, 'r')
    competitorDataStrings  = map(lambda x: string.strip(x), subf.readlines())
    
    
    for competitorstring in competitorDataStrings:
        data = competitorstring.split()
        datadict = {'teamname': data[0], 'teamid': data[1], 'teamjar': data[2], 'teamclass': data[3]}
        competitorData.append(datadict)
    
    
    ''' generate match structure - all combinations of <num_players_on_map> teams will be generated, 
        a random ordering will be determined and a match will be played '''
        
    matchid = 0
    num_competitors = len(competitorData)
    competitor_indexes = range(num_competitors)
    
    for mapp in maplist:
        for index_combination in itertools.combinations(competitor_indexes, num_players_on_map):
            gamestruct[matchid] = Game(num_players_on_map, mapp)
            
            ## generate random permutation of player ids 1 - 4
            rand_player_ids = range(1, num_players_on_map + 1)
            random.shuffle(rand_player_ids)
            
            for i in range(num_players_on_map):
                team_idx = index_combination[i]
                
                player_id = rand_player_ids[i]
                team = TeamDataInGame(competitorData[team_idx]['teamname'], 
                                   competitorData[team_idx]['teamid'], 
                                   competitorData[team_idx]['teamjar'], 
                                   competitorData[team_idx]['teamclass'], 
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
            os.makedirs(JOBS + "/" + str(matchid) + "/a1")
            os.makedirs(JOBS + "/" + str(matchid) + "/a2")
            
            # create server directory structure 
            current_server_dir = JOBS + "/" + str(matchid) + "/s"
            os.symlink(FRAMEWORK + "/cqserver/maps/", current_server_dir + "/maps")
            os.symlink(FRAMEWORK + "/cqserver/lib/", current_server_dir + "/lib")
            #os.symlink(FRAMEWORK + "/cqserver/images/", current_server_dir + "/images")
            os.symlink(FRAMEWORK + "/cqserver/GamePolicy.xml", current_server_dir + "/GamePolicy.xml")
            os.symlink(FRAMEWORK + "/cqserver/logging.properties", current_server_dir + "/logging.properties")
            os.symlink(FRAMEWORK + "/cqserver/cqserver.jar", current_server_dir + "/cqserver.jar")
            os.symlink(FRAMEWORK + "/cqserver/secrets.txt", current_server_dir + "/secrets.txt")
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


def generate_reports():
    from django.conf import settings
    
    settings.configure()
    os.chdir(SCRIPTS)
    
    ### build teamlist
    teamscoringlist = []
    
    n = len(team_stats.keys())
    nrgames = len(maplist) * 2 * (n - 1)
    
    
    for cdata in competitorData:
        teamid = cdata['teamid']
        scoredata = team_stats[teamid]
        
        scoredata['averagescore'] /= nrgames
        d = dict(scoredata)
        d['id'] = teamid
        teamscoringlist.append(d)
    
    
    for i in range(len(teamscoringlist)):
        scoredata = teamscoringlist[i]
        
        for amap in maplist:
            mapentry = amap.split(".")[0]
            scoredata['games'][mapentry].insert(i, {})
    
    for teamitem in teamscoringlist:
        gen_report_per_team(teamitem, teamscoringlist)
    

def gen_report_per_team(teamitem, teamscoringlist):
    from django.template import Template, Context
    
    tplf = open("report_tex.tpl", "r")
    tpl_string = ""
    for line in tplf.readlines():
        tpl_string += line
    tplf.close()
    
    thisteamid = teamitem['id']
    thisteamname = teamitem['name']
    
    t = Template(tpl_string)
    c = Context({'thisteamid': thisteamid, 'thisteamname': thisteamname, 'teamscores': teamscoringlist})
    texreport = t.render(c)
    
    f = open(thisteamname + "_report.tex", "w")
    print >>f, texreport
    f.close()
    

if __name__ == '__main__':
    try:
        submissions_filename = sys.argv[1]
        num_players_on_map = sys.argv[2]
    except:
        print "Usage: python create_games.py <submissions_filename> <num_players_on_map>"
        exit(1)
    
    main(submissions_filename, num_players_on_map)