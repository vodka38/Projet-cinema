pipeline {
  agent any
  stages {
    stage('Build & Test') {
      steps {
        script {
          if (isUnix()) {
            sh './mvnw -B -V clean verify'
          } else {
            bat 'mvnw.cmd -B -V clean verify'
          }
        }
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
          reportDir: 'target/site/jacoco', reportFiles: 'index.html', reportName: 'JaCoCo'])
      }
    }
  }
}
