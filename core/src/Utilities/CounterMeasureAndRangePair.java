package Utilities;

import Equipables.CounterMeasure;

public class CounterMeasureAndRangePair
{
	public int m_rangeIndex;
	public CounterMeasure m_counterMeasure;
	
	public CounterMeasureAndRangePair( CounterMeasure c, int index)
	{
		m_rangeIndex = index;
		m_counterMeasure = c;
	}

}
