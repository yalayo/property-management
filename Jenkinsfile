pipeline {
  agent any

  environment {
    AWS_REGION   = 'us-east-1'
    ECR_REGISTRY = '000000000000.dkr.ecr.us-east-1.amazonaws.com'
    IMAGE        = "${ECR_REGISTRY}/property-management-app"
  }

  options {
    disableConcurrentBuilds()
  }

  stages {
    stage('Build & push image') {
      steps {
        script { env.TAG = env.GIT_COMMIT.take(7) }
        dir('projects/main') {
          sh """
              clojure -T:jib jibbit.core/build :tag '"${env.TAG}"' :allow-insecure-registries true
          """
        }
      }
    }
  }
}