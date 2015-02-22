cd ..

rm -rf doc

javadoc \
 -tagletpath __dist/sdedit-jar/sdedit-4.2-SNAPSHOT.jar \
 -classpath __dist/sdedit-jar/sdedit-4.2-SNAPSHOT.jar \
 -taglet net.sf.sdedit.taglet.SequenceTaglet \
 -sourcepath src/main/java \
 -encoding utf-8 \
 -docencoding utf-8 \
 -d doc \
net.sf.sdedit.taglet
