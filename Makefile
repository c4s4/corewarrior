BUILD_DIR = build
COMMANDE  = javac -O -d $(BUILD_DIR) #-Xlint:deprecation -Xlint:unchecked

all: jar

clean: # Clean generated files
	rm -rf $(BUILD_DIR)

.PHONY: build
build: # Build source code
	$(COMMANDE) src/casa/corewarrior/compilateur/*.java \
		src/casa/corewarrior/editeur/*.java \
		src/casa/corewarrior/moteur/*.java \
		src/casa/awt/*.java

resources: # Add resources in build
	cp src/Manifest.txt $(BUILD_DIR)
	mkdir -p $(BUILD_DIR)/img
	cp img/*.gif $(BUILD_DIR)/img
	mkdir -p $(BUILD_DIR)/aide
	cp src/aide.txt $(BUILD_DIR)/aide/

jar: clean build resources # Build jar file
	cd $(BUILD_DIR) && jar cfm corewarrior.jar *
