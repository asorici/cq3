#!/usr/bin/python

import sys, os, string, time

ROOT = os.path.abspath(os.getcwd() + "/../")
SCRIPTS = ROOT + "/scripts"

RESULTS_DUMP_FILE_PREFIX = "result_dump_exactly_4"
# RESULTS_DUMP_FILE = ROOT + "/result_dump.txt"

TEAM_REPORT_TEMPLATE = SCRIPTS + "/report_tex_final.tpl"

## TODO: get it from sysargs
GROUP_FOLDER = ROOT +"/final_round"


# maps
maplist = ['map_cq3_v1.cqm',
           'map_cq3_v2.cqm',
           'map_cq3_v3.cqm'
           ]

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

    RESULTS_DUMP_FILE = ROOT + "/" + RESULTS_DUMP_FILE_PREFIX +\
        "_submissions_file_" +\
        submissions_filename + ".txt"

    # read result dump and load team statistics
    dumpf = open(RESULTS_DUMP_FILE, 'r')
    team_stats_by_map = simplejson.load(dumpf)
    dumpf.close()

    print "#### Generating reports ####"
    generate_report(team_stats_by_map)
    

def generate_report(team_stats_by_map):
    from django.conf import settings

    settings.configure()
    os.chdir(SCRIPTS)

    ''' build general overview list '''
    (team_overview_scores_list, tosd) = build_overview_list(team_stats_by_map)
    games = extract_games_per_map(team_stats_by_map)
    gen_report(team_overview_scores_list, games)

def escape_latex_text(string):
    for l, i in latex_escape.items():
        string = string.replace(l, i)
    return string

def sys_ok(string):
    return string.replace("~","_")

def gen_report(teamscoringlist, games):
    import jinja2
    t_file = open(TEAM_REPORT_TEMPLATE, "r")
    t_string = reduce(lambda s1,s2 : s1+s2, t_file.readlines())
    t_file.close()

    print repr(games)

    template = jinja2.Template(t_string)
    tex_content = template.render(
                                  teamscores = teamscoringlist,
                                  gamescores = games
                                  )
    # print repr(team_item)
    tex_file = open(GROUP_FOLDER + "/"  + "semifinal_report.tex", "w")
    print >>tex_file, tex_content
    tex_file.close()

def extract_games_per_map(team_stats_by_map):
    games_per_map = {}
    for map_name, all_team_stats in team_stats_by_map.items():
        games_per_map[map_name] = {}
        m = games_per_map[map_name]
        m['ename'] = escape_latex_text(map_name)
        m['games'] = {}
        first_team = all_team_stats.values()[0]
        print first_team.keys()
        i = 0
        for g in first_team['games']:
            z = 0
            m['games'][i] = {}
            m['games'][i][g['points'] * -10 + z] = {}
            ft = m['games'][i][g['points'] * -10 + z]
            
            ft['ename'] = escape_latex_text(first_team['team_name'])
            ks = ['dead_units', 'kills', 'total_score', 'placed_traps',\
                      'placed_traps', 'dead_units', 'first_blood',\
                      'retaliation_kills', 'placed_towers', 'killing_sprees',\
                      'successful_traps', 'finish_position', 'points',\
                      'finish_position', 'player_id']
            for k in ks:
                ft[k] = g[k]
            print len(g['oponent_data'])
            for o in g['oponent_data']:
                z += 1
                m['games'][i][o['points'] * -10 + z] = {}
                t = m['games'][i][o['points'] * -10 + z]
                t['ename'] = escape_latex_text(o['team_name'])
                for k in ks:
                    t[k] = o[k]
            print m['games'][i].keys()
            i += 1
    return games_per_map
                                           
                                           
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
                team_overview_scores[team_id]['p1'] += team_overview['p1']
                team_overview_scores[team_id]['p2'] += team_overview['p2']
                team_overview_scores[team_id]['p3'] += team_overview['p3']
                team_overview_scores[team_id]['p4'] += team_overview['p4']
            else:
                team_overview_scores[team_id] = dict(**team_overview)
    i = 0
    for k in team_overview_scores.keys():
        i = i + 1
        team_overview_scores[k]['label'] = "T" + repr(i)
        team_overview_scores[k]['ename'] = escape_latex_text(team_overview_scores[k]['team_name'])
    
    sorted_overview = sorted(team_overview_scores.values(),
                             key = lambda d: d['total_points'], reverse = True)
    return (sorted_overview, team_overview_scores)


def build_map_overview_dict(all_team_stats):
    team_map_overview_scores = {}

    for team_id, team_stats in all_team_stats.items():
        team_overview_dict = {'team_id': str(team_id),
                              'team_name': team_stats['team_name'],
                              'total_points' : team_stats['total_points'],
                              'p1': 0,
                              'p2': 0,
                              'p3': 0,
                              'p4': 0
                              }

        for game in team_stats['games']:
            if game['points'] == 0:
                team_overview_dict['p4'] += 1
            elif game['points'] == 1:
                team_overview_dict['p3'] += 1
            elif game['points'] == 2:
                team_overview_dict['p2'] += 1
            elif game['points'] == 3:
                team_overview_dict['p1'] += 1

        team_map_overview_scores[int(team_id)] = team_overview_dict

    return team_map_overview_scores

if __name__ == '__main__':
    try:
        submissions_filename = sys.argv[1]
    except:
        print "Usage: python create_games_one_on_one.py <submissions_filename>"
        exit(1)
    main(submissions_filename)
