import sys, os, string, time

ROOT = os.path.abspath(os.getcwd() + "/../")
SCRIPTS = ROOT + "/scripts"
RESULT_DUMP_FILE = ROOT + "/result_dump.txt"

# maps
maplist = ['map_cq3_v1.cqm']

# starting position dictionary
positions_dict = {1: "ULC", 
                  2: "URC", 
                  3: "LLC", 
                  4: "LRC"}

# point results dictionary
point_result_dict = {0: "L",
                     1: "D",
                     2: "W"}


## sorted list (from best to worst) of general overview results (w, d, l, total_score) for each team
team_overview_scores_list = []


def main():
    import simplejson
    
    # read result dump and load team statistics
    dumpf = open(RESULT_DUMP_FILE, 'r')
    team_stats_by_map = simplejson.load(dumpf)
    dumpf.close()
    
    
    print "#### Generating reports ####"
    generate_reports(team_stats_by_map)


def generate_reports(team_stats_by_map):
    from django.conf import settings
    
    settings.configure()
    os.chdir(SCRIPTS)
    
    ### build general overview list
    team_overview_scores_list = build_overview_list(team_stats_by_map)
    
    print team_overview_scores_list
    
    """
    n = len(teamscoring.keys())
    nrgames = len(maplist) * 2 * (n - 1)
    
    for cdata in competitorData:
        teamid = cdata['teamid']
        scoredata = teamscoring[teamid]
        
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
    """

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
                

def build_overview_list(team_stats_by_map):
    team_overview_scores = {}
    
    for map_name, all_team_stats in team_stats_by_map.items():
        team_map_overview_scores = build_map_overview_dict(all_team_stats)
        
        for team_id, team_overview in team_map_overview_scores.items():
            if team_id in team_overview_scores:
                team_overview_scores[team_id]['total_points'] += team_overview['total_points']
                team_overview_scores[team_id]['w'] += team_overview['w']
                team_overview_scores[team_id]['d'] += team_overview['d']
                team_overview_scores[team_id]['l'] += team_overview['l']
            else:
                team_overview_scores[team_id] = dict(**team_overview) 
    
    sorted_overview = sorted(team_overview_scores.values(), key = lambda d: d['total_points'], reverse = True)
    return sorted_overview
        

def build_map_overview_dict(all_team_stats):
    team_map_overview_scores = {}
    
    for team_id, team_stats in all_team_stats.items():
        team_overview_dict = {'team_id': int(team_id),
                              'team_name': team_stats['team_name'], 
                              'total_points' : team_stats['total_points'],
                              'w': 0,
                              'd': 0,
                              'l': 0
                              }
        
        for game in team_stats['games']:
            if game['points'] == 0:
                team_overview_dict['l'] += 1
            elif game['points'] == 1:
                team_overview_dict['d'] += 1
            elif game['points'] == 2:
                team_overview_dict['w'] += 1
        
        team_map_overview_scores[int(team_id)] = team_overview_dict
    
    return team_map_overview_scores

if __name__ == '__main__':
    main()