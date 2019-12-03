def mntgroup = [
    [ host:"EPBYMINW4141", student:"Anton Pavlovsky", branch: "apavlovsky" ],
    [ host:"EPBYMINW5961", student:"Aleksandr Shkraba", branch: "ashkraba" ],
    [ host:"EPBYMINW6229", student:"Vladislav Tarasevich", branch: "vtarasevich" ],
    [ host:"EPBYMINW6852", student:"Mikhail Elizarov", branch: "melizarov" ],
    [ host:"EPBYMINW7240", student:"Andrei Nikitsenka", branch: "anikitsenka" ],
    [ host:"EPBYMINW7785", student:"Andrei Shvedau", branch: "ashvedau" ],
    [ host:"EPBYMINW8538", student:"Dzmitry Prusevich", branch: "dprusevich" ],
    [ host:"EPBYMINW8778", student:"Pavel Hardzeyeu", branch: "phardzeyeu" ],
    [ host:"EPBYMINW9093", student:"Igor Gantman", branch: "igantman" ],
    [ host:"EPBYMINW9128", student:"Stsiapan Hanchar", branch: "shanchar" ],
    [ host:"EPBYMINW9137", student:"Sergei Kudrenko", branch: "skudrenko" ],
    [ host:"EPBYMINW9138", student:"Yury Kachatkou", branch: "ykachatkou" ],
    [ host:"EPBYMINW9139", student:"Yauheni Matveichyk", branch: "ymatveichyk" ],
    [ host:"EPBYMINW9140", student:"Vitali Markau", branch: "vmarkau" ],
    [ host:"EPBYMINW9141", student:"Igor Bletsko", branch: "ibletsko" ],
    [ host:"EPBYMINW9146", student:"Anton Yanchuk", branch: "ayanchuk" ],
    [ host:"EPBYMINW9147", student:"Egor Komarov", branch: "ekomarov" ],
    [ host:"EPBYMINW9149", student:"Aliaksandr Miasnikovich", branch: "amiasnikovich" ]
]

def mntgroup = []

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
              scm('H/30 * * * *')
          }
          definition {
            cpsScm {
                    scriptPath("Jenkinsfile")
                  scm {
                      git {
                         remote { 
                          if (task == "pipeline") { 
                              url("https://github.com/MNT-Lab/p193e-module.git")
                          } 
                          else {
                              url("https://github.com/MNT-Lab/d193l-module.git")
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
