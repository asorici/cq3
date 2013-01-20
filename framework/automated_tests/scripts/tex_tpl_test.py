maplist = ['map_v1.cqm']

def main():
    from django.conf import settings
    #from django.template.loader import render_to_string
    
    settings.configure()    

    teamscoringlist = []
    teamscoringlist.append({'id': '1', 'name':'Dragon', 'w': 2, 'd': 0, 'l':0 , 's': 4, 'averagescore': 1000})
    teamscoringlist.append({'id': '2', 'name':'tempname', 'w': 0, 'd': 0, 'l':2 , 's': 0, 'averagescore': 100})
    
    teamscoringlist[0]['games'] = {'map_v1' : [{'me': 1500, 'op': 150}], 'map_v2': [{'me': 1500, 'op': 150}]}
    teamscoringlist[1]['games'] = {'map_v1': [{'me': 50, 'op': 500}], 'map_v2': [{'me': 50, 'op': 500}] } 
    
    for i in range(len(teamscoringlist)):
        scoredata = teamscoringlist[i]
        scoredata['games']['map_v1'].insert(i, {})
        scoredata['games']['map_v2'].insert(i, {})
    
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
    #texreport = render_to_string('report_tex.tpl', {'thisteamid': thisteamid, 'teamscores': teamscoringlist})
    
    t = Template(tpl_string)
    c = Context({'thisteamid': thisteamid, 'teamscores': teamscoringlist})
    texreport = t.render(c)
    
    f = open(thisteamname + "_report.tex", "w")
    print >>f, texreport
    f.close()
    
if __name__ == '__main__':
    main()
