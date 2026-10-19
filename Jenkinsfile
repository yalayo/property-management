pipeline {
  agent any

  environment {
    AWS_REGION   = 'us-east-1'
    ECR_REGISTRY = '000000000000.dkr.ecr.us-east-1.amazonaws.com'
    IMAGE        = "${ECR_REGISTRY}/property-management-app"
  }

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  stages {

    stage('Uberjar') {
      steps { sh 'clojure -T:build uber' }
    }

    stage('Build & push image') {
      steps {
        script { env.TAG = env.GIT_COMMIT.take(7) }
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
                          credentialsId: 'aws-ecr']]) {
          sh '''
            jib jar \
              --from=eclipse-temurin:21-jre \
              --target="$IMAGE:$TAG" \
              --additional-tags=latest \
              --to-credential-helper=ecr-login \
              target/app-standalone.jar
          '''
        }
      }
    }
  }
}