#!/usr/bin/python

import sys, os, string, time

ROOT = os.path.abspath(os.getcwd() + "/../")
SCRIPTS = ROOT + "/scripts"

RESULTS_DUMP_FILE_PREFIX = "result_dump_one_on_one"
# RESULTS_DUMP_FILE = ROOT + "/result_dump.txt"

TEAM_REPORT_TEMPLATE = SCRIPTS + "/report_tex.tpl"

## TODO: get it from sysargs
GROUP_FOLDER = ROOT +"/results_test_round_2"


# maps
maplist = ['map_cq3_v1.cqm']

# starting position dictionary
positions_dict = {1: "ULC",
                  2: "LRC",
                  3: "URC",
                  4: "LLC"}

# point results dictionary
point_result_dict = {0: "L",
                     1: "D",
                     2: "W"}


latex_escape = {'_' : '\\_',
                '~' : '\textasciitilde'}

## sorted list (from best to worst) of general overview results
## (w, d, l, total_score) for each team
team_overview_scores_list = []


def main(submissions_filename):
    import simplejson

    RESULTS_DUMP_FILE = ROOT + "/" + RESULTS_DUMP_FILE_PREFIX + "_" +\
        submissions_filename + ".txt"

    # read result dump and load team statistics
    dumpf = open(RESULTS_DUMP_FILE, 'r')
    team_stats_by_map = simplejson.load(dumpf)
    dumpf.close()

    print "#### Generating reports ####"
    generate_reports(team_stats_by_map)


def generate_reports(team_stats_by_map):
    from django.conf import settings

    settings.configure()
    os.chdir(SCRIPTS)

    ''' build general overview list '''
    (team_overview_scores_list, tosd) = build_overview_list(team_stats_by_map)

    for map_name, all_team_stats in team_stats_by_map.items():
        for team_id, team_overview in all_team_stats.items():
            team_overview['games'] =\
                map(lambda g :
                        dict(g,**{'oponent_data':
                                      map(lambda d :
                                              dict(d,**{'label':
                                                            tosd[d['team_id']]['label']}),
                                          g['oponent_data'])}),
                    team_overview['games'])

    print team_overview_scores_list
    print "TO DO"
    ''' INSERT CONTINUATION HERE '''
    ### TUDOR BERARIU: Asta doar pentru runda asta, cand avem o singura harta
    ### De schimbat
    for team_id, team_overview in team_stats_by_map.values()[0].items():
        gen_report_per_team(team_overview, team_overview_scores_list)

def escape_latex_text(string):
    for l, i in latex_escape.items():
        string = string.replace(l, i)
    return string

def sys_ok(string):
    return string.replace("~","_")

def gen_report_per_team(team_item, teamscoringlist):
    import jinja2
    t_file = open(TEAM_REPORT_TEMPLATE, "r")
    t_string = reduce(lambda s1,s2 : s1+s2, t_file.readlines())
    t_file.close()

    team_name = team_item["team_name"]
    
    ## Probabil nu va mai fi cazul atunci cand vor juca mai multi pe harta
    ops = list(set(map(lambda g : g['oponent_data'][0]['label'], team_item['games'])))
    team_item["ops"] = ops
    
    team_id =\
        filter(lambda d : d["team_name"] == team_name,
               teamscoringlist)[0]['team_id']

    print "Generating report for " + repr(team_id) + "." +team_name

    template = jinja2.Template(t_string)
    tex_content = template.render(thisteamname = team_name,
                                  escapedteamname = escape_latex_text(team_name),
                                  thisteamid = team_id,
                                  teamscores = teamscoringlist,
                                  teamitem = team_item
                                  )
    print repr(team_item)
    tex_file = open(GROUP_FOLDER + "/" + sys_ok(team_name) + "_report.tex", "w")
    print >>tex_file, tex_content
    tex_file.close()

def build_overview_list(team_stats_by_map):
    """
    Takes as input the de-serialized json encoding of team game statistics
    (organized by maps).
    Creates the sorted overview list for the results of a one-on-one list of
    matches.
    The returned list of dictionaries contains the number of wins, draws,
    losses and total_points.
    """
    team_overview_scores = {}

    for map_name, all_team_stats in team_stats_by_map.items():
        team_map_overview_scores = build_map_overview_dict(all_team_stats)
        for team_id, team_overview in team_map_overview_scores.items():
            if team_id in team_overview_scores:
                team_overview_scores[team_id]['total_points'] +=\
                    team_overview['total_points']
                team_overview_scores[team_id]['w'] += team_overview['w']
                team_overview_scores[team_id]['d'] += team_overview['d']
                team_overview_scores[team_id]['l'] += team_overview['l']
            else:
                team_overview_scores[team_id] = dict(**team_overview)
    i = 0
    for k in team_overview_scores.keys():
        i = i + 1
        team_overview_scores[k]['label'] = "T" + repr(i)
    
    sorted_overview = sorted(team_overview_scores.values(),
                             key = lambda d: d['total_points'], reverse = True)
    return (sorted_overview, team_overview_scores)


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
    try:
        submissions_filename = sys.argv[1]
    except:
        print "Usage: python create_games_one_on_one.py <submissions_filename>"
        exit(1)
    main(submissions_filename)
