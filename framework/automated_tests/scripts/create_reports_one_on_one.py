import sys, os, string, time

#ROOT = os.path.expanduser('~') + "/aiwo/wo-crafting-quest/framework/automated_tests"
RESULT_DUMP_FILE = "result_dump.txt"
SCRIPTS = ROOT + "/scripts"
SUBSFILE = SCRIPTS + "/submissions_file"
SUBS = ROOT + "/submissions"
FRAMEWORK = ROOT + "/cqframework"
JOBS = ROOT + "/jobs"
GROUP_FOLDER = "group"

# maps
maplist = ['map.cqm']

# competitor data and game structure
competitorData = []
gamestruct = {}
teamscoring = {}


def main(subsfilename, jobs_folder):
    # read team data and generate game structure
    SUBSFILE = SCRIPTS + "/" + subsfilename
    JOBS = ROOT + "/" + jobs_folder
    
    subf = open(SUBSFILE, 'r')
    competitorDataStrings  = map(lambda x: string.strip(x), subf.readlines())
    
    for competitorstring in competitorDataStrings:
        data = competitorstring.split()
        datadict = {'teamname': data[0], 'teamid': data[1], 'teamjar': data[2], 'teamclass': data[3]}
        competitorData.append(datadict)
        teamscoring[ data[1] ] = {'name': data[0], 'w': 0, 'd': 0, 'l': 0, 's':0, 'averagescore': 0}
        
        teamscoring[ data[1] ]['games'] = {}
        for amap in maplist:
            mapentry = amap.split(".")[0]
            teamscoring[ data[1] ]['games'][mapentry] = []

    #### generate match structure
    matchid = 0
    for mapp in maplist:
        for i in range(len(competitorData)):
            for j in range(len(competitorData)):
                if (i != j):
                    gamestruct[matchid] = {}
                    matchData = {}
                    
                    # team 1 data
                    matchData['teamid1'] = competitorData[i]['teamid']
                    matchData['teamname1'] = competitorData[i]['teamname']
                    matchData['teamjar1'] = competitorData[i]['teamjar']
                    matchData['teamclass1'] = competitorData[i]['teamclass']
                    matchData['teamsecret1'] = 100
                    
                    # team 2 data
                    matchData['teamid2'] = competitorData[j]['teamid']
                    matchData['teamname2'] = competitorData[j]['teamname']
                    matchData['teamjar2'] = competitorData[j]['teamjar']
                    matchData['teamclass2'] = competitorData[j]['teamclass']
                    matchData['teamsecret2'] = 200
                    
                    # general match data
                    matchData['servername'] = "CraftingQuest"
                    matchData['serverhost'] = "localhost"
                    matchData['serverport'] = "1198"
                    
                    gamestruct[matchid][mapp] = matchData
                    
                    matchid += 1
    
    for matchid, mapMatchData in gamestruct.items():
        for mapp, matchData in mapMatchData.items():
            current_server_dir = JOBS + "/" + str(matchid) + "/s"
            collect_score(current_server_dir, mapp.split(".")[0], matchData)

    print "#### Generating reports ####"
    generate_reports()
            
def collect_score(current_server_dir, mapp, matchData):
    os.chdir(current_server_dir)
    
    try:
        f = open("winner.txt", "r")
        filecontents = map(lambda x: string.strip(x), f.readlines())
        
        if filecontents[0] == "winner":
            winnerContents = filecontents[1].split()
            winnerSecret = int(winnerContents[1])
            winnerPoints = int(winnerContents[2])
            
            loserContents = filecontents[3].split()
            loserSecret = int(loserContents[1])
            loserPoints = int(loserContents[2])
            
            matchData['result'] = "win"
            
            if matchData['teamsecret1'] == winnerSecret:
                matchData['winnername'] = matchData['teamname1']
                matchData['winnerid'] = matchData['teamid1']
                matchData['losername'] = matchData['teamname2']
                matchData['loserid'] = matchData['teamid2']
                
                teamscoring[ matchData['teamid1'] ]['games'][mapp].append({'me': winnerPoints, 'op': loserPoints})
                
            else:
                matchData['winnername'] = matchData['teamname2']
                matchData['winnerid'] = matchData['teamid2']
                matchData['losername'] = matchData['teamname1']
                matchData['loserid'] = matchData['teamid1']
                
                teamscoring[ matchData['teamid1'] ]['games'][mapp].append({'me': loserPoints, 'op': winnerPoints})
            
            teamscoring[ matchData['winnerid'] ]['w'] += 1
            teamscoring[ matchData['winnerid'] ]['s'] += 2
            teamscoring[ matchData['winnerid'] ]['averagescore'] += winnerPoints
            teamscoring[ matchData['loserid'] ]['l'] += 1
            teamscoring[ matchData['loserid'] ]['averagescore'] += loserPoints
            
            matchData['winnerpoints'] = winnerPoints
            matchData['loserpoints'] = loserPoints
        else:
            tiepoints = int(filecontents[1])
            matchData['result'] = "tie"
            matchData['tiepoints'] = tiepoints
            
            teamscoring[ matchData['teamid1'] ]['d'] += 1
            teamscoring[ matchData['teamid1'] ]['s'] += 1
            teamscoring[ matchData['teamid1'] ]['averagescore'] += tiepoints
            teamscoring[ matchData['teamid2'] ]['d'] += 1
            teamscoring[ matchData['teamid2'] ]['s'] += 1
            teamscoring[ matchData['teamid2'] ]['averagescore'] += tiepoints
            
            teamscoring[ matchData['teamid1'] ]['games'][mapp].append({'me': tiepoints, 'op': tiepoints})
             
    except Exception, ex:
        print "Score collect failed for " + matchData['teamname1'] + " vs " + matchData['teamname2'] + ". Reason: ", ex


def generate_reports():
    from django.conf import settings
    
    settings.configure()
    os.chdir(SCRIPTS)
    
    ### build teamlist
    teamscoringlist = []
    
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
    try:
        subsfile = sys.argv[1]
        jobs_folder = sys.argv[2]
    except:
        print "No jobs folder given. Usage python create_reports.py <subsfile> <jobs_folder>"
    
    main(subsfile, jobs_folder)