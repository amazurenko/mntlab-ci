def mntgroup = [
    [ host:"EPBYMINW2470", student:"Dzmitry Yurchanka", branch: "dyurchanka" ],
    [ host:"EPBYMINW1374", student:"Raman Bahatyrou", branch: "morbid98" ],
    [ host:"EPBYMINW2472", student:"Yuliya Yemelyanovich", branch: "YuliyaYemelyanovich" ],
    [ host:"EPBYMINW2473", student:"Yauheni Dzmitryieu", branch: "bon3142" ],
    [ host:"EPBYMINW2033", student:"Viachaslau Kuftyrau", branch: "kftrv" ],
    [ host:"EPBYMINW2471", student:"Filip Chkhaidze", branch: "filler36" ],
    [ host:"EPBYMINW6122", student:"Aliaksandr Ramanovich", branch: "xander-rom" ],
    [ host:"EPBYMINW7423", student:"Ilya Khrysanfau", branch: "Hrisanfov" ],
    [ host:"EPBYMINW7296", student:"Aliaksei Tsyuliou", branch: "Tsyuliou" ],
    [ host:"EPBYMINW0501", student:"Pavel Sauchanka", branch: "Pursu1tOfHapp1ness" ],
    [ host:"EPBYMINW2629", student:"Sergei TiT", branch: "ttsrg" ],
    [ host:"EPBYMINW2476", student:"Andrei Charviakou", branch: "XxXantivirusXxX" ],
    [ host:"EPBYMINW5878", student:"Natallia Shynkevich", branch: "fdc333447" ]

/*
    [ host:"EPBYMINW3088", student:"No Name", branch: "nname"],
    [ host:"EPBYMINW2466", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW1969", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2467", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2695", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW1766", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW7425", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW2468", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3092", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW3093", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6405", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6406", student:"No Name", branch: "nname" ],
    [ host:"EPBYMINW6593", student:"No Name", branch: "nname" ],
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
