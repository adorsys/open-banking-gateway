PUML_URLS_PATTERN=http://www\.plantuml\.com.*develop/docs/\(.*\).puml&fmt=svg&vvv=1&sanitize=true
PUML_URLS_REPLACE=https://adorsys.github.io/open-banking-gateway/doc/${TRAVIS_TAG}/\1.png

site: clean_docs replace_puml_urls convert_puml build_docs copy_puml

.PHONY : replace_puml_urls
replace_puml_urls:
	cp README.md docs/README.md
	sed -i 's/docs\///g' docs/README.md
	find . -type f -name "*.md" -print -exec sed -i.bak 's%${PUML_URLS_PATTERN}%${PUML_URLS_REPLACE}%' {} \;
	find . -type f -name "*.md.bak" -print -delete

.PHONY : convert_puml
convert_puml:
	cd docs && plantuml **/*.puml

.PHONY : build_docs
build_docs: clean_docs
	mkdocs build --verbose

.PHONY : clean_docs
clean_docs:
	rm -rf site

.PHONY : copy_puml
copy_puml:
	cd docs && rsync -armR --include="*/" --include="*.puml" --include="*.png" --exclude="*" . ../site/
