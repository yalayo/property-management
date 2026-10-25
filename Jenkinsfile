pipeline {
  agent any

  environment {
    AWS_REGION            = 'us-east-1'
    AWS_DEFAULT_REGION    = 'us-east-1'
    AWS_ENDPOINT_URL      = 'http://floci:4566'
    AWS_ACCESS_KEY_ID     = 'test'
    AWS_SECRET_ACCESS_KEY = 'test'
    REPO_NAME             = 'property-management'
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