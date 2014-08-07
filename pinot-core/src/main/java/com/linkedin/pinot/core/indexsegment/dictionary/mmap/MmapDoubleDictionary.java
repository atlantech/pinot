package com.linkedin.pinot.core.indexsegment.dictionary.mmap;

import java.io.File;
import java.io.IOException;

import com.linkedin.pinot.core.indexsegment.columnar.creator.V1Constants;
import com.linkedin.pinot.core.indexsegment.dictionary.Dictionary;
import com.linkedin.pinot.core.indexsegment.utils.GenericRowColumnDataFileReader;
import com.linkedin.pinot.core.indexsegment.utils.SearchableByteBufferUtil;


public class MmapDoubleDictionary extends Dictionary<Double> {

  GenericRowColumnDataFileReader mmappedFile;
  SearchableByteBufferUtil searchableMmapFile;
  int size;

  public MmapDoubleDictionary(File dictionaryFile, int dictionarySize) throws IOException {
    mmappedFile =
        GenericRowColumnDataFileReader.forMmap(dictionaryFile, dictionarySize, 1,
            V1Constants.Dict.DOUBLE_DICTIONARY_COL_SIZE);
    searchableMmapFile = new SearchableByteBufferUtil(mmappedFile);
    this.size = dictionarySize;
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) <= -1 ? false : true;
  }

  public Double searchableValue(Object e) {
    if (e == null)
      return new Double(V1Constants.Numbers.NULL_DOUBLE);
    if (e instanceof Long)
      return (Double) e;
    else
      return new Double(Double.parseDouble(e.toString()));
  }

  @Override
  public int indexOf(Object o) {
    return searchableMmapFile.binarySearch(0, searchableValue(o), 0, size);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Double getRaw(int index) {
    // TODO Auto-generated method stub
    return new Double(mmappedFile.getDouble(index, 0));
  }

  @Override
  public String getString(int index) {
    // TODO Auto-generated method stub
    return String.valueOf(mmappedFile.getDouble(index, 0));
  }

}