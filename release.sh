#!/bin/bash

set -e

VERSION=$1
TAG=v$VERSION
PROJECT_LIST=flixel-core,flixel-android,flixel-desktop,flixel-html5,flixel-robovm,plugins/accelerometer,plugins/controllers,plugins/gesture

# update the POM files with the version number
./mvnw versions:set -DskipTests -DnewVersion=$VERSION -DgenerateBackupPoms=false -T2 -pl $PROJECT_LIST -am

# build jar, sources, javadoc, and push to sonatype for staging
./mvnw deploy -Prelease -T2 -pl $PROJECT_LIST -am

# commit the changes VC
git add .
git commit -m "Release v. $VERSION"
git tag -a $TAG -m "Release v. $VERSION"
git push
git push origin $TAG
    

