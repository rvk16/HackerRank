pipeline {
  agent {label 'aia-cores'}
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(daysToKeepStr: '2'))
  }
  parameters{
      choice(name: 'run_staging',          choices:['YES','NO'],                        description: 'Do you want to run staging?')
      choice(name: 'pipeline_type',        choices:['SNAPSHOT','RELEASE'],              description: 'Choose your pipeline type:')
      choice(name: 'build-and-unittests',  choices:['YES','NO'],                        description: 'Do you want to build-and-unittests?')
booleanParam(name: 'OVERRIDE_EXISTING_TAG',defaultValue: true,                          description: 'override existing tag?')
      string(name: 'CURRENT_VERSION',      defaultValue: 'TRUNK-SNAPSHOT',              description: 'current version?')
      string(name: 'RELEASE_VERSION',      defaultValue: '21.03',                       description: 'release version?')
booleanParam(name: 'run_deployment',       defaultValue: true,                          description: 'trigger oc deployment?')
booleanParam(name: 'run_Integration_test', defaultValue: true,                         description: 'trigger IT tests?')
booleanParam(name: 'run_cleanup',          defaultValue: true,                          description: 'trigger oc cleanup?')
      string(name: 'testK8sPropsFile',     defaultValue: 'test-k8s.properties',         description: 'test cluster properties file')
      string(name: 'testEnvPropsFile',     defaultValue: 'env-k8s.properties',          description: 'test env properties file')
      choice(name: 'k8sHost',              choices: ['adhng-k8s-1','adhngtest-k8s-1','heena-k8s-1','yash-k8s-1'],  description: 'kubernetes host name')
      choice(name: 'k8sHost_IT_test',      choices: ['adhngtest-k8s-1','adhng-k8s-1','heena-k8s-1','yash-k8s-1'],  description: 'kubernetes host name')
      string(name: 'nsPrefix',             defaultValue: 'adhng',                       description: 'Namespace prefix, the shorter the better')
  }
  environment {
    DOCKER_HOST = 'unix:///var/run/docker.sock'
    JENKINS_NAME = 'Jenkins'
  }
  stages {
    stage('get slave details') {
      steps {
        sh '''
          echo "Running on slave `cat /home/jenkins/slaveHostname`, Raised up POD name `hostname`"
        '''
      }
    }
    stage('get commit details') {
      steps {
        script {
          COMMIT_HASH_SHORT = sh (script: "git rev-parse --short=7 HEAD", returnStdout: true)
          PIPELINE = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[3].split("\\.")[0]
        }
      }
    }
    stage('prepare-release') {
      when { expression { params.pipeline_type == 'RELEASE' } }
      steps {
        script {
          env.PHASE='prepare-release'
          sshagent (credentials: ['jenkins-generated-ssh-key']) {
            sh '''
              git config --global user.name "kobiki"
              git config --global user.email kobi.kisos@amdocs.com
              export TAG_NAME=v${RELEASE_VERSION}
              chmod 777 ./release/tag-release.sh
              ./release/tag-release.sh PREPARE_RELEASE
            '''
          }
        }
      }
    }
    stage('remove-current-docker-images-from-build-server') {
      steps {
        script {
          env.PHASE='remove-current-docker-images-from-build-server'
        }
        sh '''
          bash /BD/DevOps/Generic_Tools/remove_images_from_server.sh il
          bash /BD/DevOps/Generic_Tools/remove_images_from_server.sh spark
        '''
      }
    }
    stage('build-and-unittests') {
      steps {
        script {
          env.PHASE='build-and-unittests'
        }
        sh 'mvn -T 2 clean install -B -U -e -Punit-test -Pjacoco'
      }
    }

    stage('deploy artifacts') {
      when { expression { return (params.pipeline_type == 'RELEASE' || env.GIT_BRANCH == 'master') } }
      steps {
        script {
          env.PHASE='deploy artifacts'
        }
        sh 'mvn deploy -B -Pno-test'
      }
    }
    stage('push-docker-images') {
      parallel {
        stage("push-aia-il-sqlite-service-image") {
          when {
            expression { params.pipeline_type != 'RELEASE' }
          }
          steps {
            script {
              env.PHASE='push-aia-il-sqlite-service'
            }
            sh '''
              cd aia-il-sqlite-service/aia-il-sqlite-dist
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -DskipTests $REG
            '''
          }
        }
        stage("push-aia-il-sqlite-service-image-release") {
          when {
           expression { params.pipeline_type == 'RELEASE' }
          }
          steps {
            script {
              env.PHASE='push-aia-il-sqlite-service-release'
            }
            sh '''
              cd aia-il-sqlite-service/aia-il-sqlite-dist
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -Prelease -DskipTests
            '''
          }
        }
        stage("push-configuration-service-image") {
          when { expression { params.pipeline_type != 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-configuration-service'
            }
            sh '''
              export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                  BRANCH=""
                  export REG="-Ddocker.push.registry=10.232.52.103:5000/artifactory/docker/temp/aiail"
                else
                  BRANCH="$BRANCH-"
                  export REG=""
                fi
              cd aia-il-configuration-service/aia-il-configuration-dist
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -DskipTests -e -X $REG
            '''
          }
        }
        stage("push-configuration-service-image-release") {
          when { expression { params.pipeline_type == 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-configuration-service-release'
            }
            sh '''
               export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                  BRANCH=""
                else
                  BRANCH="$BRANCH-"
                fi
              cd aia-il-configuration-service/aia-il-configuration-dist
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -Prelease -DskipTests
            '''
          }
        }
        stage("push-deployer-image") {
          when { expression { params.pipeline_type != 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-deployer'
            }
            sh '''
              export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                 BRANCH=""
                  export REG="-Ddocker.push.registry=10.232.52.103:5000/artifactory/docker/temp/aiail"
                else
                 BRANCH="$BRANCH-"
                  export REG=""
                fi
              cd aia-il-deployer-parent/aia-il-deployer
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -DskipTests $REG
            '''
          }
        }
        stage("push-deployer-image-release") {
          when { expression { params.pipeline_type == 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-deployer-release'
            }
            sh '''
              export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                 BRANCH=""
                else
                 BRANCH="$BRANCH-"
                fi
              cd aia-il-deployer-parent/aia-il-deployer
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -Prelease -DskipTests
            '''
          }
        }
        stage("push-collector-deployer-image") {
          when { expression { params.pipeline_type != 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-deployer'
            }
            sh '''
              export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                 BRANCH=""
                  export REG="-Ddocker.push.registry=10.232.52.103:5000/artifactory/docker/temp/aiail"
                else
                 BRANCH="$BRANCH-"
                  export REG=""
                fi
              cd aia-il-deployer-parent/aia-il-collector-deployer
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -DskipTests $REG
            '''
          }
        }
        stage("push-collector-deployer-image-release") {
          when { expression { params.pipeline_type == 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-deployer-release'
            }
            sh '''
              export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                 BRANCH=""
                else
                 BRANCH="$BRANCH-"
                fi
              cd aia-il-deployer-parent/aia-il-collector-deployer
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -Prelease -DskipTests
            '''
          }
        }
        stage("push-busdeployer-image") {
          when { expression { params.pipeline_type != 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-busdeployer'
            }
            sh '''
              export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                 BRANCH=""
                  export REG="-Ddocker.push.registry=10.232.52.103:5000/artifactory/docker/temp/aiail"
                else
                 BRANCH="$BRANCH-"
                  export REG=""
                fi
              cd aia-il-deployer-parent/aia-il-busdeployer
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -DskipTests $REG
            '''
          }
        }
        stage("push-busdeployer-image-release") {
          when { expression { params.pipeline_type == 'RELEASE' } }
          steps {
            script {
              env.PHASE='push-busdeployer-release'
            }
            sh '''
              export
                BRANCH=$GIT_BRANCH
                if [[ "$BRANCH" == "master" ]]
                then
                 BRANCH=""
                else
                 BRANCH="$BRANCH-"
                fi
              cd aia-il-deployer-parent/aia-il-busdeployer
              mvn deploy -B -Dbranch=$BRANCH -Pdocker -Prelease -DskipTests
            '''
          }
        }
      }
    }
            stage("push-rest-invoker-job-image") {
              when { expression { params.pipeline_type != 'RELEASE' } }
              steps {
                script {
                  env.PHASE='push-rest-invoker-job'
                }
                sh '''
                  export
                    BRANCH=$GIT_BRANCH
                    if [[ "$BRANCH" == "master" ]]
                    then
                     BRANCH=""
                      export REG="-Ddocker.push.registry=10.232.52.103:5000/artifactory/docker/temp/aiail"
                    else
                     BRANCH="$BRANCH-"
                      export REG=""
                    fi
                  cd aia-il-rest-invoker/aia-il-rest-invoker-job
                  mvn deploy -B -Dbranch=$BRANCH -Pdocker -DskipTests $REG
                '''
              }
            }
            stage("push-rest-invoker-job-image-release") {
              when { expression { params.pipeline_type == 'RELEASE' } }
              steps {
                script {
                  env.PHASE='push-rest-invoker-job-release'
                }
                sh '''
                  export
                    BRANCH=$GIT_BRANCH
                    if [[ "$BRANCH" == "master" ]]
                    then
                     BRANCH=""
                    else
                     BRANCH="$BRANCH-"
                    fi
                  cd aia-il-rest-invoker/aia-il-rest-invoker-job
                  mvn deploy -B -Dbranch=$BRANCH -Pdocker -Prelease -DskipTests
                '''
              }
            }
    stage ('Run IT and Sonar') {
        parallel {
          stage ('Run IT') {
            stages {
                stage ('K8s deployment') {
                  when { expression { params.run_deployment == true && env.GIT_BRANCH == 'master' && params.run_Integration_test == true } }
                      steps {
                        script {
                          TEMP_IMAGE_FILE = ""
                          if (env.GIT_BRANCH == "master" ) {
                            TEMP_IMAGE_FILE = "aia-aiail-temp-images.csv"
                          }
                          if (env.GIT_BRANCH.length() > 7) {
                            GIT_BRANCH_SHORT = "${env.GIT_BRANCH.substring(0,7)}"
                          }
                          else {
                            GIT_BRANCH_SHORT = "${env.GIT_BRANCH}"
                          }
                          build job: 'aia-deployment-start/master',
                          parameters: [string(name: 'K8S_MASTER_NODE',          value: "${params.k8sHost_IT_test}"),
                                       string(name: 'NAMESPACE',                value: "${params.nsPrefix}-${GIT_BRANCH_SHORT}"),
                                       string(name: 'AIA_VERSION',              value: 'TRUNK-SNAPSHOT'),
                                       string(name: 'PROPS_FILE',               value: 'properties_file.adhngtest-k8s-1.txt'),
                                       string(name: 'CLUSTER_TYPE',             value: 'k8s'),
                                 booleanParam(name: 'AIA_TLS',                  value: 'false'),
                                 booleanParam(name: 'CASSANDRA_TLS',            value: 'false'),
                                       string(name: 'security_roles',           value: 'false'),
                                       string(name: 'stabled_version',          value: 'latest'),
                                       string(name: 'cleanup_aia_jobs',         value: 'YES'),
                                       string(name: 'install_spark_operator',   value: 'NO'),
                                       string(name: 'install_airflow',          value: 'NO'),
                                       string(name: 'install_keda',             value: 'YES'),
                                       string(name: 'IL',                       value: 'true'),
                                        string(name: 'UI',                     value: 'true'),
                                 booleanParam(name: 'RUNTIME',                  value: true),
                                 booleanParam(name: 'AUTHORING',                value: true),
                                       string(name: 'TEMP_IMAGE_FILE',          value: "${TEMP_IMAGE_FILE}"),
                                       string(name: 'run_migration',            value: 'NO'),]
                        }
                      }
                }
                stage('integration-tests') {
                  when { expression { params.run_Integration_test == true } }
                  steps {
                    script {
                      env.PHASE='configuration-integration-tests'
                    }
                    sh '''
                      export HOST_IP=`cat /home/jenkins/slaveHostname`
                      mvn integration-test -Pconfiguration-integration-test -Daia.test.properties=test.properties -Daia.test.mode=remote
                      mvn failsafe:verify -Pconfiguration-integration-test
                    '''
                  }
                }
              }
            }
            stage('sonar') {
              steps {
                script {
                  env.PHASE='sonar'
                }
                sh '''
                  if [ ${pipeline_type} = RELEASE ]
                  then
                    REL=${RELEASE_VERSION}
                  else
                    REL=${GIT_BRANCH}-${CURRENT_VERSION}
                  fi
                  mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.8.0.2131:sonar -Dsonar.host.url=http://illin5648.corp.amdocs.com:30606 -Dsonar.login=5c2c1ebbf523e608e390896eedff8c7b7dc0b744 -Dsonar.projectVersion=${REL}
                '''
              }
            }
	      }
	 }
	stage('copy images to latest') {
      when { expression { return (env.GIT_BRANCH == 'master') } }
      steps {
        script {
          env.PHASE='copy images to latest'
        }
        sh '''
          bash /BD/GIT/aia-devops/aia-deployment/scripts/run_copy_docker_image_aiail.sh -d
        '''
      }
    }
  }
  post {
    success {
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'aia-il-reports/target/site/jacoco-aggregate', reportFiles: 'index.html', reportName: 'Coverage Report', reportTitles: 'Coverage'])
      echo 'I succeeded!'
      script {
        if (env.BRANCH_NAME == "master" && run_staging == "YES" && pipeline_type != "RELEASE") {
          echo 'will trigger'
          echo String.valueOf(COMMIT_HASH_SHORT)
          echo String.valueOf(PIPELINE)
          build job: 'aia-il-collectors/master', wait: false
          build job: 'aia-il-publisher/master', wait: false
        } else {
          echo 'will not trigger'
        }
      }
    }
    failure {
      echo 'I Failed!'
      script {
        if (env.BRANCH_NAME == "master"){
          mail to: 'AiaPlatformAnalytics@int.amdocs.com, DigitalIntelligenceDVCIRDFT1@int.amdocs.com, DigitalIntelligenceDVCIRDFT2@int.amdocs.com',
          subject: "Failed Pipeline: '${currentBuild.fullDisplayName}' ,Pipeline Stage: '${env.PHASE}'",
          body: "Check this URL: ${env.BUILD_URL}."
        }
      }
    }
  }
}
