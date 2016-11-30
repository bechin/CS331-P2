import java.util.Random;
import java.util.Arrays;

public class Selection{

	private int[] array;
	private Random random = new Random();
	private double[] multi_s = {2.5, 2.0, 2.0};

	public static void main(String[] args){
		new Selection();
	}

	public Selection(){
		System.out.println("Table(Milliseconds)");
		System.out.printf("%20s|%19s|%19s|%19s|%19s\n", "Size",
		  "Classic", "IterativePartition",
		  "RecursivePartition", "Partition with MM");
		for(int i = 0; i < 100; i++)
			System.out.print("-");
		System.out.println();
		int size = 10;
		int thisMulti = 0;
		while(size < 1000000){ //CHANGE THIS LATER
			array = new int[size];
			long classicTtlAvg = 0;
			long iteratPartTtlAvg = 0;
			long recursPartTtlAvg = 0;
			long mmPartTtlAvg = 0;
			for(int i = 0; i < 10; i++){
				for(int j = 0; j < size; j++)
					array[j] = random.nextInt(100);
				classicTtlAvg += classic5Trials();
				iteratPartTtlAvg += iteratPart5Trials();
				recursPartTtlAvg += recursPart5Trials();
				mmPartTtlAvg += mmPart5Trials();
			}
			System.out.printf("%20d", size);
			System.out.printf("|%19d", classicTtlAvg /= 10);
			System.out.printf("|%19d", iteratPartTtlAvg /= 10);
			System.out.printf("|%19d", recursPartTtlAvg /= 10);
			System.out.printf("|%19d\n", mmPartTtlAvg /= 10);
			size *= multi_s[thisMulti++];
			thisMulti %= 3;
		}
	}

	private long classic5Trials(){
		long startTime = System.currentTimeMillis();
		classic(1);
		classic(array.length/4);
		classic(array.length/2);
		classic(array.length*3/4);
		classic(array.length);
		return (System.currentTimeMillis()-startTime)/5;
	}

	private long iteratPart5Trials(){
		long startTime = System.currentTimeMillis();
		iteratPart(1);
		iteratPart(array.length/4);
		iteratPart(array.length/2);
		iteratPart(array.length*3/4);
		iteratPart(array.length);
		return (System.currentTimeMillis()-startTime)/5;
	}

	private long recursPart5Trials(){
		long startTime = System.currentTimeMillis();
		recursPart(1);
		recursPart(array.length/4);
		recursPart(array.length/2);
		recursPart(array.length*3/4);
		recursPart(array.length);
		return (System.currentTimeMillis()-startTime)/5;
	}

	private long mmPart5Trials(){
		long startTime = System.currentTimeMillis();
/*		mmPart(1);
		mmPart(array.length/4);
		mmPart(array.length/2);
		mmPart(array.length*3/4);
		mmPart(array.length);*/
		return (System.currentTimeMillis()-startTime)/5;
	}

	private int classic(int k){
		int[] thisCopy = mergeSort(array.clone());
		return thisCopy[k-1];
	}

	private int[] mergeSort(int[] a){
		if(a.length==1)
			return a;
		int mid = a.length/2;
		int[] firstHalf = mergeSort(Arrays.copyOfRange(a, 0, mid));
		int[] secondHalf = mergeSort(Arrays.copyOfRange(a, mid, a.length));
		return merge(firstHalf, secondHalf);
	}

	private int[] merge(int[] a, int[] b){
		int[] result = new int[a.length + b.length];
		int i = 0, aPtr = 0, bPtr = 0;
		while(aPtr < a.length && bPtr < b.length){
			if(a[aPtr] <= b[bPtr])
				result[i++] = a[aPtr++];
			else
				result[i++] = b[bPtr++];
		}
		while(aPtr < a.length)
			result[i++] = a[aPtr++];
		while(bPtr < b.length)
			result[i++] = b[bPtr++];
		return result;
	}

	private boolean verify(int[] a){
		for(int i = 0; i < a.length-1; i++){
			if(a[i]>a[i+1])
				return false;
		}
		return true;
	}

	private int iteratPart(int k){
		int[] thisCopy = array.clone();
		int start = 0;
		int end = thisCopy.length-1;
		int pivPos;
		do{
			pivPos = partition(thisCopy, start, end);
			if(pivPos == k-1)
				return thisCopy[k-1];
			else if(k-1 < pivPos)
				end = pivPos - 1;
			else
				start = pivPos + 1;
		}while(pivPos != k-1);
		return Integer.MAX_VALUE; //compiler wanted a return outside of loop...
	}

	private int partition(int[] a, int start, int end){
		//last element is pivot
		if(start == end)
			return start;
		int pivot = a[end];
		int finPivPos = start;
		for(int i = start; i < end; i++){
			if(a[i] <= pivot)
				swap(a, i , finPivPos++);
		}
		swap(a, finPivPos, end);
		return finPivPos;
	}

	private void swap(int[] a, int b, int c){
		int temp = a[b];
		a[b] = a[c];
		a[c] = temp;
	}

	private int recursPart(int k){
		int[] thisCopy = array.clone();
		return recursivePartition(thisCopy, 0, thisCopy.length-1, k);
	}

	private int recursivePartition(int[] a, int start, int end, int k){
		int pivPos = partition(a, start, end);
		if(pivPos == k-1)
			return a[k-1];
		else if(k-1 < pivPos)
			return recursivePartition(a, start, --pivPos, k);
		else
			return recursivePartition(a, ++pivPos, end, k);
	}

	private int mmPart(int k){
		int[] thisCopy = array.clone();
		return mmPartition(thisCopy, k);
	}

	private int mmPartition(int[] a, int k){
		if(a.length <= 100){
			Arrays.sort(a);
			return a[k-1];
		}
		int numOfSubsets = a.length/100;
		int[] medians = new int[numOfSubsets];
		for(int i = 0; i < numOfSubsets; i++){
			int[] subset = Arrays.copyOfRange(a, 100*i, 100*(i+1));
			medians[i] = mmPartition(subset, 50);
		}
		int mmPivot = mmPartition(medians, numOfSubsets/2);
		int mmPivPos = 0;
		while(a[mmPivPos] != mmPivot)
			mmPivPos++;
		swap(a, mmPivPos, a.length-1);
		mmPivPos = partition(a, 0, a.length-1);
		if(k-1 == mmPivPos)
			return a[mmPivPos];
		else if(k-1 < mmPivPos){
			int[] S = Arrays.copyOfRange(a, 0, mmPivPos-1);
			return mmPartition(S, k);
		}
		int[] R = Arrays.copyOfRange(a, mmPivPos+1, a.length-1);
		return mmPartition(R, k);
	}	

}
