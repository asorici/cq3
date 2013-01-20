import sys, os, string, time

ROOT = os.path.expanduser('~') + "/aiwo/wo-crafting-quest/framework/automated_tests"
SCRIPTS = ROOT + "/scripts"
RESULT_DUMP_FILE = "result_dump.txt"

# maps
maplist = ['map_cq3_v1.cqm']



def main():
    import simplejson
    
    # read result dump and load team statistics
    dumpf = open(RESULT_DUMP_FILE, 'r')
    team_stats = simplejson.load(dumpf)
    dumpf.close()
    
    
    print "#### Generating reports ####"
    generate_reports(team_stats)


def generate_reports(team_stats):
    from django.conf import settings
    
    settings.configure()
    os.chdir(SCRIPTS)
    
    ## build team rankings list
    team_scores = map(lambda x:  {'team_id': x[0],
                                  'team_name': x[1]['team_name'],
                                  'total_points': x[1]['total_points']
                                  }, team_stats.items())
    
    sorted_team_scores = sorted(team_scores, key = lambda d: d['total_points'], reverse = True)
    
    for team_id in team_stats.keys():
        print str(team_id) + " " + team_stats[team_id]['team_name']
        for game_result in team_stats[team_id]['games']["1"]:
            outcome = "w"
            if game_result['finish_position'] == 1 and game_result['points'] == 1:
                outcome = "win"
            elif game_result['finish_position'] == 2 and game_result['points'] == 1:
                outcome = "draw"
            else:
                outcome = "lose"
            print "vs: ", game_result['oponent_data'], ": " + outcome
        
        for game_result in team_stats[team_id]['games']["2"]:
            outcome = "w"
            if game_result['finish_position'] == 1 and game_result['points'] == 1:
                outcome = "win"
            elif game_result['finish_position'] == 2 and game_result['points'] == 1:
                outcome = "draw"
            else:
                outcome = "lose"
            print "vs: ", game_result['oponent_data'], ": " + outcome
        
        print ""
    
    print sorted_team_scores
    

def gen_report_per_team(teamitem, teamscoringlist):
    from django.template import Template, Context
    
    tplf = open("report_tex_qualifications.tpl", "r")
    tpl_string = ""
    for line in tplf.readlines():
        tpl_string += line
    tplf.close()
    
    thisteamid = teamitem['id']
    thisteamname = teamitem['name']
    
    t = Template(tpl_string)
    c = Context({'thisteamid': thisteamid, 'thisteamname': thisteamname, 'teamscores': teamscoringlist})
    texreport = t.render(c)
    
    f = open(GROUP_FOLDER + "/" + thisteamname + "_report.tex", "w")
    print >>f, texreport
    f.close()
                

if __name__ == '__main__':
    main()