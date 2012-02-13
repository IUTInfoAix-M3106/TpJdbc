SRC = TpJDBC

.PHONY: all $(DIR)

all: ${SRC}.pdf $(DIR)

$(DIR):
	$(MAKE) -C $@ $(MAKECMDGOALS)

clean: 
	@echo "suppression des fichiers de compilation"
	@# fichiers de compilation latex
	@rm -f *.log *.aux *.dvi *.toc *.lot *.lof
	@# fichiers de bibtex
	@rm -f *.bbl *.blg

complet: $(DIR)
	@echo "compilation complete"
	rubber -f -d ${SRC}
initial: clean
	@echo „suppression des fichiers cibles“
	@rm -f ${SRC}.ps ${SRC}.pdf
	
%.pdf : %.tex
	@echo "compilation du tex"
	rubber -d ${SRC}
	@echo "Citations ou références indéfinies:"
	@egrep -i $(UNDEFINED) $*.log || echo "Aucune"
