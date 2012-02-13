SVG2PDF = ../svg2pdf
SVG = $(wildcard *.svg)
PDF = $(patsubst %.svg,%.pdf,  $(SVG))

.PHONY:all
all:$(PDF)
clean:
	rm -f *.pdf
%.pdf: %.svg
	${SVG2PDF} $< $*.pdf
