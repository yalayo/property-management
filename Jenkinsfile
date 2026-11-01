pipeline {
  agent any

  environment {
    AWS_REGION            = 'us-east-1'
    AWS_DEFAULT_REGION    = 'us-east-1'
    AWS_ENDPOINT_URL      = 'http://floci:4566'
    AWS_ACCESS_KEY_ID     = 'test'
    AWS_SECRET_ACCESS_KEY = 'test'
    REPO_NAME             = 'property-management'

    // GitOps repo (no https:// prefix - the token is inserted in front of it)
    GITOPS_REPO           = 'github.com/yalayo/gitops.git'
    GITOPS_BRANCH         = 'main'
    GITOPS_VALUES         = 'envs/floci/values.yaml'
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

    stage('Verify image in ECR') {
      steps {
        // Never point GitOps at a tag the cluster can't pull
        sh 'aws ecr describe-images --repository-name "$REPO_NAME" --image-ids imageTag="$TAG"'
      }
    }

    stage('Promote to GitOps (floci)') {
      steps {
        // Jenkins credential "github-gitops" (Username with password):
        //   username = your GitHub user, password = fine-grained PAT with Contents=read/write on the GitOps repo
        withCredentials([usernamePassword(credentialsId: 'github-token',
                                          usernameVariable: 'GIT_USER',
                                          passwordVariable: 'GIT_TOKEN')]) {
          sh '''
            set -eu
            rm -rf gitops
            git clone --branch "$GITOPS_BRANCH" "https://${GIT_USER}:${GIT_TOKEN}@${GITOPS_REPO}" gitops
            cd gitops

            # Replace the tag on the line marked "# jenkins-image-tag"
            sed -i -E 's|^([[:space:]]*tag:[[:space:]]*).*(# jenkins-image-tag.*)$|\\1"'"$TAG"'" \\2|' "$GITOPS_VALUES"

            if git diff --quiet -- "$GITOPS_VALUES"; then
              echo "Tag $TAG is already in $GITOPS_VALUES - nothing to promote"
              exit 0
            fi
            git diff -- "$GITOPS_VALUES"

            git -c user.name="jenkins" -c user.email="jenkins@localhost" \
                commit -m "property-management(floci): deploy $TAG" -- "$GITOPS_VALUES"

            # Retry if someone else pushed in the meantime
            n=0
            until git push origin "HEAD:$GITOPS_BRANCH"; do
              n=$((n + 1))
              [ "$n" -ge 3 ] && { echo "push failed 3 times"; exit 1; }
              git pull --rebase origin "$GITOPS_BRANCH"
            done
          '''
        }
      }
    }
  }

  post {
    success {
      echo "Promoted ${env.TAG}. Argo CD syncs it within ~1 min: http://argocd.localhost:8080"
    }
    always {
      sh 'rm -rf gitops' // don't leave the token-bearing clone in the workspace
    }
  }
}
