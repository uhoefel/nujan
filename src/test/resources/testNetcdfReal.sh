#!/bin/sh


# Validates the NetCDF4 layer using real data.
#
# Runs TestNetcdfa.java on various configurations and compares the
# output with saved known good files.
#
# For usage, see badparms below.
#

NCJAR=/d1/steves/ftp/netcdfJava/netcdfAll-4.2.jar
BUILDDIR=../../../target/test-classes:../../../target/classes
PKGBASE=edu.ucar.ral.nujan
TESTDIR=.


badparms() {
  echo Error: $1
  echo Parms:
  echo "  compress:  all / compressLevel (0==none, 1 - 9)"
  echo "  inDir"
  echo "  subDir"
  echo "  fileSpec:  all / fileName"
  echo "  bugs       optional: none / echo / update"
  echo ""
  echo "Examples:"
  echo "./testNetcdfReal.sh v1 0 testNetcdfRealData/samples a gfs-grb2.nc"
  exit 1
}



if [ $# -ne 4 -a $# -ne 5 ]; then badparms "wrong num parms"; fi

compressSpec=$1
inDir=$2
subDir=$3
fileSpec=$4
bugs=none
if [ $# -eq 5 ]; then bugs=$5; fi

if [ "$compressSpec" == "all" ]; then compressVals="0 5"
else compressVals="$compressSpec"
fi

if [ "$fileSpec" == "all" ]; then
  files=$(/bin/ls $inDir/$subDir)
else
  files=$fileSpec
fi


echo "compressVals: $compressVals"
echo "inDir: $inDir"
echo "subDir: $subDir"
echo "files: $files"




# Copy to testRealOut
if [ "$bugs" == "update" ]; then
  for ifile in $files; do
    inPath=$inDir/$subDir/$ifile
    echo ===== start $inPath =====
    ###h5dump -p -w 10000 $inPath >  tempout.newa
    ###echo '===== ncdump ====='  >> tempout.newa
    ncdump $inPath | sed '1s/netcdf .*{/netcdf someFile {/' > tempout.newa
    gzip -c tempout.newa > testRealOut/test.$subDir.$ifile.out.gz
  done
  exit 0
fi

for compress in $compressVals; do
  for ifile in $files; do
    inPath=$inDir/$subDir/$ifile
    echo "===== start compress $compress  inPath $inPath ====="
    configMsg="./testNetcdfReal.sh $compress $inDir $subDir $ifile"

    /bin/rm -f tempb.nc
    copyCmd="java -cp ${BUILDDIR}:${NCJAR} \
      ${PKGBASE}.netcdfTest.NhCopy \
      -bugs 0 \
      -compress $compress \
      -inFile $inPath \
      -outFile tempb.nc"
    if [ "$bugs" != "none" ]; then echo "copyCmd: $copyCmd"; fi
    $copyCmd > tempb.log
    if [ "$?" -ne "0" ]; then
      echo "NhCopy failed for config: $configMsg"
      echo "  copyCmd: $copyCmd"
      exit 1
    fi

    echo "  test: $configMsg  size: $(wc -c tempb.nc | cut -f 1 -d ' ')"

    ncdump tempb.nc | sed '1s/netcdf .*{/netcdf someFile {/' > tempout.newa
    if [ "$?" -ne "0" ]; then
      echo "ncdump failed for config: $configMsg"
      echo "  copyCmd: $copyCmd"
      exit 1
    fi

    oldTxt=${TESTDIR}/testNetcdfRealOut/test.$subDir.$ifile.out.gz
    zcat $oldTxt > tempout.olda
    diffCmd="diff tempout.olda tempout.newa"
    $diffCmd
    #xxx if [ "$?" -ne "0" ]; then
    #xxx   echo "ncdump failed for config: $configMsg"
    #xxx   echo "  copyCmd: $copyCmd"
    #xxx   echo "  diffCmd: $diffCmd"
    #xxx   exit 1
    #xxx fi

  done
done

