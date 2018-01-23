package sort;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/22 9:52
 * @Project tio-http-server
 */
public class FastSort {

    public static void main(String[] args){
        int[] ints = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
        fastSort(ints);

       int searchIndex = search(ints,15);
        System.out.println("找到的索引值为:"+searchIndex);
    }

    /**
     * 快速排序
     * */
    private static void fastSort(int[] ints){
        if(ints.length==0){return;}
        //第一遍循环，找出比上一个值小的那个索引
        for (int i = 1;i < ints.length; i++){
            if (ints[i] < ints[i-1]){
                //第二遍循环，替换位置,
                int temp;
                for (int j = i;j>=0;j--){
                    if(ints[j-1]>ints[j]){
                        temp = ints[j];
                        //交换位置
                        ints[j] = ints[j-1];
                        ints[j-1] = temp;
                    }else{
                        //剩下的数序一致不用在比较
                        break;
                    }
                }
            }
        }
        for (int i=0;i<ints.length;i++){
            System.out.println(ints[i]);
        }
    }

    /**
     * 二分查找法
     * 取中间值，然后比较，大，小，等于，直到比较出结果
     * */
    private static int search(int[] ints,int value){
        int startIndex = 0;
        int endIndex = ints.length - 1;
        int middleIndex;

        while (startIndex <= endIndex){
            //取出中间索引值
            middleIndex = (startIndex + endIndex) / 2;
            //如果value就和中间值相等，直接找到了，返回 O(1)
            if(value==ints[middleIndex]){
                return middleIndex;
            }
            if(value > ints[middleIndex]){
                //值大于中间值，从右边开始(+1 排除middleIndex的值)
                startIndex = middleIndex + 1;
            }else{
                //值小于中间值，从左边开始找（-1排除middleIndex的值）
                endIndex = middleIndex - 1;
            }
        }
        //如果运行到这里还没有返回值，说明数组里面没有
        return -1;
    }
}
