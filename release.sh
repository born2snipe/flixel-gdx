VERSION=$1
TAG=v$VERSION

./mvnw versions:set -DskipTests -DnewVersion=$VERSION -DgenerateBackupPoms=false -T2 \
    && ./mvnw deploy -Prelease -T2 \
    && git add . \
    && git commit -m "Release v. $VERSION" \
    && git tag -a $TAG -m "Release v. $VERSION" \
    && git push \
    && git push origin $TAG
    

