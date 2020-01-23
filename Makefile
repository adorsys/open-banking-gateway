PUML_URLS_PATTERN=http://www\.plantuml\.com.*develop/docs/\(.*\).puml&fmt=svg&vvv=1&sanitize=true
PUML_URLS_REPLACE=../../\1.png

.PHONY : all
all: java fintech-ui site

site: 	clean_docs \
	prepare_docs \
	replace_puml_urls \
	convert_puml \
	build_docs \
	copy_puml

.PHONY : clean_docs
clean_docs:
	# "makefile: clean"
	rm -rf site

.PHONY : prepare_docs
prepare_docs:
	# "makefile: prepare_docs"
	rm -rf docs_for_site
	mkdir docs_for_site
	cp -r docs docs_for_site/

.PHONY : replace_puml_urls
replace_puml_urls:
	# "makefile: replace_puml_urls"
	cp mkdocs.yml docs_for_site
	cp README.md docs_for_site/docs/README.md
# trick with bak-files works for sed of GNU and BSD, therefore the command is macos and linux compatible
	sed -i.bak 's/docs\///g' docs_for_site/docs/README.md && rm -rf docs_for_site/docs/README.md.bak
	find docs_for_site -type f -name "*.md" -exec sed -i.bak 's/\.\.\/README.md/README.md/g' {} \;
	find docs_for_site -type f -name "*.md.bak" -exec rm -rf {} \;
	find docs_for_site -type f -name "*.md" -exec sed -i 's%${PUML_URLS_PATTERN}%${PUML_URLS_REPLACE}%' {} \;

.PHONY : convert_puml
convert_puml:
	# "makefile: convert_puml"
	cd docs_for_site && plantuml **/*.puml

.PHONY : build_docs
build_docs: clean_docs
	# "makefile: clean_docs"
	cd docs_for_site && mkdocs build

.PHONY : copy_puml
copy_puml:
	# "makefile: copy_puml"
	mv docs_for_site/site ./site
	rm -rf docs_for_site

.PHONY : clean_java
clean_java:
	mvn -T1C clean

.PHONY : java
java: clean_java java_tests
	mvn -T1C -DskipTests install

.PHONY : java_tests
java_tests:
	mvn verify

fintech-ui/node_modules:
	cd fintech-examples/fintech-ui && npm install

.PHONY : fintech-ui
fintech-ui: fintech-ui/node_modules
	cd fintech-examples/fintech-ui && npm run build:prod


