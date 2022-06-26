pipeline {
    agent any

    triggers {
        pollSCM('*/3 * * * *')
    }

    environment {
        imagename = "bff_backend"
        registryCredential = 'jsoiworks'
        dockerImage = ''
    }

    stages {
        // git에서 repository clone
        stage('Prepare') {
          steps {
            echo 'Clonning Repository'
            git url: 'https://github.com/MeongPanNyaengPan-MPNP/BFF_BackEnd.git',
              branch: 'main',
              credentialsId: 'jenkins_token'
            }
            post {
             success {
               echo 'Successfully Cloned Repository'
             }
           	 failure {
               error 'This pipeline stops here...'
             }
          }
        }

        // gradle build
        stage('Bulid Gradle') {
          agent any
          steps {
            echo 'Bulid Gradle'
            dir ('.'){
                sh """
                ./gradlew clean build --exclude-task test
                """
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        // docker build
        stage('Bulid Docker') {
          agent any
          steps {
            echo 'Bulid Docker'
            script {
                dockerImage = docker.build imagename
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        // docker push
        stage('Push Docker') {
          agent any
          steps {
            echo 'Push Docker'
            script {
                docker.withRegistry( '', registryCredential) {
                    dockerImage.push("1.0")  // ex) "1.0"
                }
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }
    }
}