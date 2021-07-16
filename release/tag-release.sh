#!/bin/bash -x

export ACTION=$1
export CURRENT_BRANCH="${GIT_BRANCH}"

function exit_if_tag_exists {
    git ls-remote --tags 2>/dev/null | grep ${TAG_NAME} 1>/dev/null
     if [ "$?" == 0 ]; then
      times
      echo "[ERROR] Tag $TAG_NAME already exists in repository, you can set OVERRIDE_EXISTING_TAG=\"true\" to override this tag"
      exit "$r"
    fi
}

# Rename all version of each pom with $RELEASE_VERSION
function rename_poms {
    # Find current version from the pom.xml.
    local version="$CURRENT_VERSION"
    # Since grep return the whole line there are spaces that needed to trim.
    local trimmed_version="$(echo -e "${version}" | tr -d '[[:space:]]')"
    # Find each pom.xml and replace every $trimmed_version with RELEASE_VERSION
    find . -name "pom.xml" -exec sed -i "s/$trimmed_version/$RELEASE_VERSION/" \{\} \;
}

function rename_k8s_ymls {
    # Find current version from the k8s yml files
    local version=$CURRENT_VERSION
    # Since grep return the whole line there are spaces that needed to trim.
    local trimmed_version="$(echo -e "${version}" | tr -d '[[:space:]]')"
    # Find each k8s yml and replace every $trimmed_version with RELEASE_VERSION
    find . -name "*.yml" -exec sed -i.bak "s/:master-${trimmed_version}/:$RELEASE_VERSION/" \{\} \;
}

function rename_swagger_ymls {
    # Find current version from the swagger yaml files
    local version=$CURRENT_VERSION
    # Since grep return the whole line there are spaces that needed to trim.
    local trimmed_version="$(echo -e "${version}" | tr -d '[[:space:]]')"
    # Find each swagger yaml and replace every $trimmed_version with RELEASE_VERSION
    find . -name "swagger.yaml" -exec sed -i.bak "s/version: \"${trimmed_version}\"/version: \"${RELEASE_VERSION}\"/" \{\} \;
}

function rename_docker_files {
    # Find current version from the docker files.
    local version="$CURRENT_VERSION"
    echo "version $version"
    echo "release $RELEASE_VERSION"
    # Since grep return the whole line there are spaces that needed to trim.
    local trimmed_version="$(echo -e "\${branch}${version}" | tr -d '[[:space:]]')"
    local trimmed_version_without_branch="$(echo -e "${version}" | tr -d '[[:space:]]')"
    echo "trimmedWithBranch $trimmed_version"
    echo "trimmedWithoutBranch $trimmed_version_without_branch"
    # Find each docker file and replace every $trimmed_version with RELEASE_VERSION
    find . -name "Dockerfile" -exec sed -i.bak "s/${trimmed_version}/${RELEASE_VERSION}/" \{\} \;
    find . -name "Dockerfile" -exec sed -i.bak "s/${trimmed_version_without_branch}/${RELEASE_VERSION}/" \{\} \;
}


# Create a temporary branch for the pom changes commits.
# If such branch already exists delete it
# Do not push this branch.
function create_temp_branch {
    (
      git show-ref --verify --quiet "refs/heads/$RELEASE_VERSION"
      if [ "$?" -eq 0 ]
      then
          git branch -D  "$RELEASE_VERSION"
      fi
      git checkout -b "$RELEASE_VERSION"
    )
}

# Commit local changes and create a tag
# It assume the branch is the local temp branch,
function commit_changes {
    local msg="Modify poms to $RELEASE_VERSION in temp branch that was built on top of $CURRENT_BRANCH"
    pushd "."
    git commit -am "$msg"
    git tag -f -a "$TAG_NAME" -m "$msg"
    popd
}

# Delete the temp branch $1
# Push the tag to origin
function delete_temp_branch {
    local temp_branch="$1"
    pushd "."
    git checkout -q "$TAG_NAME"
    git branch -D "$temp_branch"
    popd
}

function push_tag {
  pushd "."
  if [ "$OVERRIDE_EXISTING_TAG" != "true" ]
    then
	    git push origin tag "$TAG_NAME"
    else
	    git push -f origin tag "$TAG_NAME"
  fi
  popd
}

function setup_release_branch {
    local temp_branch_name="$RELEASE_VERSION"
    pushd "."
    pwd

    if [ "$OVERRIDE_EXISTING_TAG" != "true" ]
    then
	    exit_if_tag_exists
    fi

    create_temp_branch

    rename_poms

    rename_k8s_ymls

    rename_swagger_ymls

    rename_docker_files

    commit_changes

	  echo "DONE."
	  popd
}

function teardown_release_branch {
  local temp_branch_name="$RELEASE_VERSION"
  pushd "."
  delete_temp_branch "$temp_branch_name"
  popd
}

function delete_tag {
  local tag_name="$TAG_NAME"
  pushd "."
  git push --delete origin "$tag_name"
  popd
}

if [ "${ACTION}" == "PREPARE_RELEASE" ]; then
    setup_release_branch
elif [ "${ACTION}" == "DELETE_TEMP_BRANCH" ]; then
    teardown_release_branch
elif [ "${ACTION}" == "PUSH_TAG" ]; then
    push_tag
elif [ "${ACTION}" == "DELETE_TAG" ]; then
    delete_tag
else
    echo "[ERROR] unknown mode ${ACTION}"
    exit 1
fi
