def mntgroup = [
    [ host:"EPBYMINW2466", student:"Andrei Skorkin", branch: "askorkin" ],
    [ host:"EPBYMINW1969", student:"Kseniya Kalesnikava", branch: "kkalesnikava" ],
    [ host:"EPBYMINW2468", student:"Yauheniya Rayetskaya", branch: "yrayetskaya" ],
    [ host:"EPBYMINW2473", student:"Anatol Isachanka", branch: "aisachanka" ],
    [ host:"EPBYMINW2695", student:"Raman Bachakrou", branch: "rbachakrou" ],
    [ host:"EPBYMINW2033", student:"Ihar Kazlouski", branch: "ikazlouski" ],
    [ host:"EPBYMINW0501", student:"Uladzislau Zabauski", branch: "uzabauski" ],
    [ host:"EPBYMINW5357", student:"Uladzislau Muraveika", branch: "umuraveika" ],
/*    
    [ host:"EPBYMINW3088", student:"No Name", branch: "nname" ],   
    [ host:"EPBYMINW2470", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2629", student:"No Name", branch: "nname" ],

    [ host:"EPBYMINW1374", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2472", student:"No Name", branch: "nname" ],

    [ host:"EPBYMINW2467", student:"No Name", branch: "nname" ],


    [ host:"EPBYMINW2033", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW1766", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW7425", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW7423", student:"No Name", branch: "nname" ],

    


    [ host:"EPBYMINW2471", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6122", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3092", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3093", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6405", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6406", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6593", student:"No Name", branch: "nname" ],

    [ host:"EPBYMINW7296", student:"Yauheni Maniukevich", branch: "ymaniukevich" ]  
 */ 
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
