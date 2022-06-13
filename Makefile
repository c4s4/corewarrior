VERSION   = 0.3.0
BUILD_DIR = build
COMMANDE  = javac -O -d $(BUILD_DIR) #-Xlint:deprecation -Xlint:unchecked

all: archive publish

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

doc: # Generate documentation
	mkdir -p $(BUILD_DIR)
	md2pdf -o $(BUILD_DIR)/README.pdf README.md

archive: jar doc # Generate distribution archive
	cd $(BUILD_DIR) && mkdir -p corewarrior-$(VERSION) && \
		mv corewarrior.jar corewarrior-$(VERSION)/ && \
		cp ../LICENSE.txt corewarrior-$(VERSION)/ && \
		mv README.pdf corewarrior-$(VERSION)/ && \
		cp ../bin/* corewarrior-$(VERSION)/ && \
		cp -r ../examples/ corewarrior-$(VERSION)/ && \
		zip -r corewarrior-$(VERSION).zip corewarrior-$(VERSION)/

publish: # Copy REAME in sweetohm site
	cp README.md $(BUILD_DIR)/corewarrior.md
	sed -i 's/(img\//(/g' $(BUILD_DIR)/corewarrior.md
	cp $(BUILD_DIR)/corewarrior.md ../sweetohm/content/article/corewarrior.md
