pipeline {
  agent any

  environment {
    APP_NAME = "springapp"
    REMOTE_USER = "ubuntu"
    REMOTE_HOST = "13.201.98.113"
    REMOTE_PATH = "/home/ubuntu/springapp"
  }

  stages {
    stage('Clone') {
      steps {
        git 'https://github.com/syednaveedfazal/curd-cicd-aws.git'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Deploy') {
      steps {
        sh """
          ssh $REMOTE_USER@$REMOTE_HOST "mkdir -p $REMOTE_PATH"
          scp target/*.jar $REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH/app.jar
          ssh $REMOTE_USER@$REMOTE_HOST "
            pkill -f 'java -jar' || true
            nohup java -jar $REMOTE_PATH/app.jar > $REMOTE_PATH/app.log 2>&1 &
          "
        """
      }
    }
  }

  post {
    success {
      echo '✅ Deployment complete'
    }
    failure {
      echo '❌ Build/Deploy failed'
    }
  }
}
