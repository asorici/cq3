#!/bin/bash

for f in *.tex
do
    pdflatex $f
    pdflatex $f
done