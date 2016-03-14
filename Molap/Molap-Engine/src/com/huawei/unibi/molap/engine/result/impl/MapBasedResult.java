package com.huawei.unibi.molap.engine.result.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.huawei.unibi.molap.constants.MolapCommonConstants;
import com.huawei.unibi.molap.engine.aggregator.MeasureAggregator;
import com.huawei.unibi.molap.engine.result.Result;
import com.huawei.unibi.molap.engine.wrappers.ByteArrayWrapper;

public class MapBasedResult implements Result<Map<ByteArrayWrapper, MeasureAggregator[]>,Void>
{
    private Iterator<Entry<ByteArrayWrapper, MeasureAggregator[]>> resultIterator;
    
    private Entry<ByteArrayWrapper, MeasureAggregator[]> tuple;
    
    private Map<ByteArrayWrapper, MeasureAggregator[]> scannerResult;
    
    private int resulSize;
    
    public MapBasedResult()
    {
        scannerResult = new HashMap<ByteArrayWrapper, MeasureAggregator[]>(MolapCommonConstants.DEFAULT_COLLECTION_SIZE);
        this.resultIterator = scannerResult.entrySet().iterator();
    }
    
    @Override
    public ByteArrayWrapper getKey()
    {
        tuple = this.resultIterator.next();
        return tuple.getKey();
    }

    @Override
    public MeasureAggregator[] getValue()
    {
        return tuple.getValue();
    }

    @Override
    public boolean hasNext()
    {
        return this.resultIterator.hasNext();
    }

    @Override
    public void addScannedResult(Map<ByteArrayWrapper, MeasureAggregator[]> scannerResult, Void v)
    {
        this.scannerResult=scannerResult;
        resulSize=scannerResult.size();
        this.resultIterator = scannerResult.entrySet().iterator();
    }

    @Override
    public void merge(Result<Map<ByteArrayWrapper,MeasureAggregator[]>,Void> result)
    {
        ByteArrayWrapper key = null;
        MeasureAggregator[] value = null;
        Map<ByteArrayWrapper,MeasureAggregator[]> otherResult = result.getKeys();
        if(otherResult!=null){
        while(resultIterator.hasNext())
        {
            Entry<ByteArrayWrapper, MeasureAggregator[]> entry = resultIterator.next();
            key = entry.getKey();
            value = entry.getValue();
            MeasureAggregator[] agg = otherResult.get(key);
            if(agg != null)
            {
                for(int j = 0;j < agg.length;j++)
                {
                    agg[j].merge(value[j]);
                }
            }
            else
            {
                otherResult.put(key, value);
            }
        }
        resulSize=otherResult.size();
        this.resultIterator=otherResult.entrySet().iterator();
        this.scannerResult=otherResult;
        }
    }

    @Override
    public int size()
    {
        return resulSize;
    }

    @Override
    public Map<ByteArrayWrapper, MeasureAggregator[]> getKeys()
    {
        return this.scannerResult;
    }

    @Override
    public Void getValues()
    {
        return null;
    }
}