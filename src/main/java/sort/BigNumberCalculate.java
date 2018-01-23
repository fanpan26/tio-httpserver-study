package sort;


/**
 * @Author fyp
 * 使用数组方式实现大数据的运算，因为 maxValue会溢出，所以采用数组
 */
public class BigNumberCalculate {

    public static void main(String[] args){
       // calculate(new int[]{0,0,0,2,5,6,8,7,9},15);
        calculate(new int[]{1,1,1,6,9,5,9,8,5,4,2,6,3,5,4,1,2,4,7,8},new int[]{1,1,1,6,9,5,9,8,5,4,2,6,3,5,4,1,2,4,7,8});
    }

    /**
     * 一个数组乘以另外一个数
     * 例如  123456789  * 15
     * */
    public static void calculate(int[] value1,int value2){
        //第一步，先计算数组内每个值和 value2的 相乘结果
        for (int i=0;i<value1.length;i++){
            value1[i] = value1[i] * value2;
        }
        //第二步，数组元素值保留最后一位 例如  123 =》 3，然后12 进位到前边一位，循环要倒序
        for (int i=value1.length-1;i>0;i--){
            value1[i-1] += value1[i] / 10;
            value1[i] = value1[i] % 10;//取余数
        }

        boolean zero = true;
        for (int i=0;i<value1.length;i++){
            if(zero && value1[i]==0){
                continue;
            }else {
                zero =false;
                System.out.print(value1[i]);
            }
        }
        System.out.println("");

        System.out.println(256879 * 15);
    }

    /**
     * 两个数组进行计算
     * */
    public static int[] calculate(int[] value1,int[] value2){

        //存放每次循环乘的结果
        int tempLength = value1.length * 2 -1;
        int[][] temp = new int[value1.length][tempLength];
        //第一步，循环遍历第一个，依次乘以 value2的值，然后要留空位
        for (int i=value1.length -1;i>=0;i--){
            int[] tempValue = new int[tempLength];
            //预留几个 0
            int zeroLength = value1.length-1-i;
            //赋值
            for (int j= value2.length-1;j>=0;j--){
                int index = tempLength - zeroLength - (value2.length-j);
                tempValue[index] = value1[i] * value2[j];
            }
            temp[i] = tempValue;
        }

        //数组对应的每个值想加，行成一个新数组
        int[] sumArr = new int[tempLength];
        for (int i=0;i<temp.length;i++){
            for (int j=0;j<temp[i].length;j++){
                sumArr[j]+= temp[i][j];
            }
        }

        for (int i=sumArr.length-1;i>0;i--){
            sumArr[i-1] += sumArr[i] / 10;
            sumArr[i] = sumArr[i] % 10;//取余数
        }
        return sumArr;
    }
}
