def mntgroup = [
    [ host:"EPBYMINW1374", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW1766", student:"Yauhen Khodzin", branch: "ykhodzin" ],
    [ host:"EPBYMINW1969", student:"Aliaksei Kunitski", branch: "akunitski" ],
    [ host:"EPBYMINW2033", student:"Kanstantsin Klimov", branch: "kklimov" ],
    [ host:"EPBYMINW2466", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2467", student:"amazurenko4tests", branch: "nname" ],
    [ host:"EPBYMINW2468", student:"Ihar Filimonau", branch: "ifilimonau" ],
    [ host:"EPBYMINW2470", student:"Aliaksandr Lahutsin", branch: "alahutsin" ],
    [ host:"EPBYMINW2471", student:"Uladzislau Valchkou", branch: "uvalchkou" ],
    [ host:"EPBYMINW2472", student:"Aleksandr Matiev", branch: "amatiev" ],
    [ host:"EPBYMINW2473", student:"Aleh Yarmalovich", branch: "ayarmalovich" ],
    [ host:"EPBYMINW2629", student:"Nikita Buzin", branch: "nbuzin" ],
    [ host:"EPBYMINW2695", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2976", student:"Aliaksandr Chernak", branch: "achernak" ],
    [ host:"EPBYMINW3088", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3092", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3093", student:"Aliaksei Semirski", branch: "asemirski" ],
    [ host:"EPBYMINW6405", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6406", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6593", student:"Valery Peshchanka", branch: "vpeshchanka" ],
    [ host:"EPBYMINW7425", student:"Aliaksandr Zaitsau", branch: "azaitsau" ],
    [ host:"EPBYMINW7423", student:"Hanna Kavaliova", branch: "hkavaliova" ],
    [ host:"EPBYMINW7296", student:"Pavel Kislouski", branch: "pkislouski" ]    
]

def job_name = "mntlab-ci"
def makeJobs = { group, tasks ->
  group.each { item ->
    if (item.student != "No Name"){
      folder(item.host) { 
        displayName(item.student)
        configure { folder ->
          folder / icon(class: 'com.github.mjdetullio.jenkins.plugins.multibranch.BallColorFolderIcon')
        }
      }
      tasks.each { task ->
        pipelineJob(item.host + "/" + job_name + "-" + task) {
          environmentVariables { envs([
            SLAVE: item.host
          ]) }
          triggers {
              scm('*/2 * * * *')
          }
          definition {
            cpsScm {
                    scriptPath("Jenkinsfile")
                  scm {
                      git {
                         remote { 
                          if (task == "pipeline") { 
                              url("https://github.com/MNT-Lab/mntlab-pipeline.git") 
                          } 
                          else {
                              url("https://github.com/MNT-Lab/mntlab-dsl.git") 
                          }
                            credentials('mntlabepam-jenkins-username-token')
                          }
                        
                         branch(item.branch)
                    }
                  }
              }
            }
          }
        }
      }
    }
  }

def makeViews = { group, tasks ->
  tasks.each { task -> 
    listView(task) {
      jobs {
        regex(".*-" + task)
        // name(job_name + "-" + task.name)
      }
      recurse(true)
      columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
      }
    }
  }
}

def days = ["dsl",  "pipeline"]

makeJobs(mntgroup, days)
makeViews(mntgroup, days)

listView("Students") {
  mntgroup.each { job ->
    jobs {
        name(job.host)
    }
  }
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
  }
}
