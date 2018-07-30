def mntgroup = [
    [ host:"EPBYMINW3088", student:"Yauheni Sokal", branch: "ysokal" ],
    [ host:"EPBYMINW2470", student:"Aliaksandr Patapniou", branch: "apatapniou" ],
    [ host:"EPBYMINW2629", student:"Uladzimir Kuchynski", branch: "ukuchynski" ],
    [ host:"EPBYMINW2466", student:"Siarhei Tsitou", branch: "stsitou" ],
    [ host:"EPBYMINW1374", student:"Mikhail Piatliou", branch: "mpiatliou" ],
    [ host:"EPBYMINW2472", student:"Hleb Viniarski", branch: "hviniarski" ],
    [ host:"EPBYMINW1969", student:"Darya Zhukova", branch: "dzhukova" ],
    [ host:"EPBYMINW2467", student:"Aliaksandr Kavaleu", branch: "akavaleu" ], 
    [ host:"EPBYMINW2473", student:"Kanstantsin Novichuk", branch: "knovichuk" ],
    [ host:"EPBYMINW2695", student:"Aliaksandr Aranski", branch: "aaranski" ],
    [ host:"EPBYMINW2033", student:"Oleg Monko", branch: "omonko" ],
    [ host:"EPBYMINW1766", student:"Yauheni Papkou", branch: "ypapkou" ],
    [ host:"EPBYMINW7425", student:"Mikhail Znak", branch: "mznak" ],
    [ host:"EPBYMINW7423", student:"Andrei Andryieuski", branch: "aandryieuski" ],
    [ host:"EPBYMINW0501", student:"Daniil Isakau", branch: "disakau" ],
    
/*
    [ host:"EPBYMINW2468", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2471", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6122", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3092", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3093", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6405", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6406", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6593", student:"No Name", branch: "nname" ],
*/
    [ host:"EPBYMINW7296", student:"Yauheni Maniukevich", branch: "ymaniukevich" ]  
  
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
