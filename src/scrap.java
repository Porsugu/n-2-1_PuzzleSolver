public class scrap {
    public static void main(String[] args) {
        String board="1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 0 15";
        String[] arr=board.split(" ");
        for(int k=0;k<arr.length;k++){
            System.out.print(arr[k]+",");
        }

    }
}
