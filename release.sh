VERSION=$1

./mvnw versions:set -DskipTests -DnewVersion=$VERSION -DgenerateBackupPoms=false -T2 \
    && ./mvnw deploy -Prelease -T2 \
    && git add . \
    && git commit -m "Release v. $VERSION" \
    && git tag -a v$VERSION -m "Release v. $VERSION" \
    && git push
    

