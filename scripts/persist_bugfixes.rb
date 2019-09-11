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

    data = CSV.read(project_csv, headers: true, return_headers: true)
    (0..(data.length-1)).each do |row|
      if  data[row].header_row? then
        p "this is the header! Let's go next"
        next
      end
      #getting the values from each row
      issuecode = data[row][0]
      revisionnumber = data[row][1]
      commitdate = data[row][2]
      issuetype = 'Bug'
      p "inserting issuecode: #{issuecode}, revisionnumber: #{revisionnumber}, commitdate: #{commitdate}, issuetype: #{issuetype}, project_name: #{project_name}"
      query = "insert into linkedissuessvn (projectname, issuecode, revisionnumber, commitdate, issuetype) values ('#{project_name}', '#{issuecode}', '#{revisionnumber}', '#{commitdate}', '#{issuetype}');"
      con.exec query
      p "inserted!"
    end
  rescue PG::Error => e
    p e.message
  ensure
    con.close
  end
end
#}}}

persist_bugfixes(project_name, project_csv)
