require 'csv'
require 'pg'

project_name = ARGV[0]
project_csv = ARGV[1]

#{{{ persist_bugfixes(project_csv)
def persist_bugfixes(project_name, project_csv)
  p 'entering the persist_bugfixes() method'
  begin
    con = PG.connect :dbname => 'szz-bailey', :user => 'postgres', :password => '123kanini123'
    p 'connection established'

    #getting all the linkedissues files from the dir to persist in the database
    allprojects = Dir['./Top100ProjectsBugFixes/*']
    allprojects.each do |project|
      projectname = get_project_name(project)
      persist_csv(project, projectname,con)
    end
  rescue PG::Error => e
    p e.message
  ensure
    con.close
  end
end
#}}}

#{{{ get_project_name()
def get_project_name(project)
  mytokens = project.split("/") #e.g., ./Top100ProjectsBugFixes/TRINIDADBugFixes.csv 
  projectname = mytokens[2] #e.g., TRINIDADBugFixes.csv
  projectname = projectname.sub('BugFixes.csv','') # TRINIDAD
  projectname = projectname.downcase # e.g., trinidad
  return projectname
end
#}}}

#{{{ persist_csv() 
def persist_csv(project, projectname, con)
  mycsv = CSV.read(project, headers:true, return_headers:true)
  mycsv.each do |csvrow|
    if csvrow.header_row? then
      p 'this is the header, let\'s go next!'
      next
    end
    issuecode = csvrow[0]
    revisionnumber = csvrow[1]
    commitdate = csvrow[2]
    issuetype = 'Bug'
    p "inserting issuecode: #{issuecode}, revisionnumber: #{revisionnumber}, commitdate: #{commitdate}, issuetype: #{issuetype}, projectname: #{projectname}"
    query = "insert into linkedissuessvn (projectname, issuecode, revisionnumber, commitdate, issuetype) values ('#{projectname}', '#{issuecode}', '#{revisionnumber}', '#{commitdate}', '#{issuetype}');"
    con.exec query
    p "inserted!"
  end
end
#}}}

persist_bugfixes(project_name, project_csv)

