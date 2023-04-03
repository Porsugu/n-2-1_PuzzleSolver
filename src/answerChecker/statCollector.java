package answerChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class statCollector {
    static class statOfDim implements Comparable<statOfDim> {
        private int dim,numRun;
        private Long sumTime;
        private Long minTime,maxTime;
        private Boolean firstIn;
        statOfDim(int dim){
            this.dim=dim;
            this.firstIn=true;
            this.sumTime=0L;
            this.numRun=0;
        }

        void add(Long runtime){
            this.sumTime+=runtime;
            if(this.firstIn){
                this.maxTime=runtime;
                this.minTime=runtime;
                this.firstIn=false;
            }
            else{
                if(maxTime<runtime){
                    maxTime=runtime;
                }
                else if(minTime>runtime){
                    minTime=runtime;
                }
            }
            numRun++;
        }
        Float getMinTime(){
            String temp=""+this.minTime/1000F;
            if(temp.length()>6){
                return Float.parseFloat(temp.substring(0,6));
            }
            return Float.parseFloat(temp);
        }
        Float getMaxTime(){
            String temp=""+this.maxTime/1000F;
            if(temp.length()>6){
                return Float.parseFloat(temp.substring(0,6));
            }
            return Float.parseFloat(temp);
        }
        Float getAVGtime(){
            String temp=((sumTime/numRun)/1000F)+"";
            if(temp.length()>6){
                return Float.parseFloat(temp.substring(0,6));
            }
            return Float.parseFloat(temp);
        }
        @Override
        public int compareTo(statOfDim o) {
            return this.dim-o.dim;
        }
    }
    private ArrayList<statOfDim> statistic;
    private Long totalRuntime;
    private ArrayList<String> errorList;

    public statCollector(){
        this.statistic=new ArrayList<>();
        this.errorList=new ArrayList<>();
        totalRuntime= 0L;
    }

    public void add(int dimension,Long runtime,String fileName,Boolean isValid,int stepUsed){
        String line,format;
        System.out.println("|------------------------------------------------------|");
        line="|"+dimension+"*"+dimension+": "+fileName;
        format="%"+(56-line.length())+"s";
        System.out.print(line);
        System.out.printf(format,"|");
        System.out.println();
        line="|Solution is valid: "+this.errorList.isEmpty();
        System.out.print(line);
        format="%"+(56-line.length())+"s";
        System.out.printf(format,"|");
        System.out.println();
        line="|Time used: "+runtime/1000F+" sec";
        System.out.print(line);
        format="%"+(56-line.length())+"s";
        System.out.printf(format,"|");
        System.out.println();
        line="|Step used: "+stepUsed;
        System.out.print(line);
        format="%"+(56-line.length())+"s";
        System.out.printf(format,"|");
        System.out.println();
        System.out.println("|------------------------------------------------------|");
        if(isValid){
            int index=0;
            while(index<this.statistic.size()){
                if(this.statistic.get(index).dim==dimension){
                    break;
                }
                else{
                    index++;
                }
            }
            if(index==this.statistic.size()){
                statOfDim temp=new statOfDim(dimension);
                temp.add(runtime);
                this.statistic.add(temp);
            }
            else{
                this.statistic.get(index).add(runtime);
            }
            totalRuntime+=runtime;
        }
        else{
            this.errorList.add(fileName);
        }

    }

    public void genTable(){
        String line,format;
        System.out.println("|------------------------------------------------------|");
        line="|All solutions are valid: "+this.errorList.isEmpty();
        System.out.print(line);
        format="%"+(56-line.length())+"s";
        System.out.printf(format,"|");
        System.out.println();
        line="|Time used: "+this.totalRuntime/1000F+" sec";
        System.out.print(line);
        format="%"+(56-line.length())+"s";
        System.out.printf(format,"|");
        System.out.println();
        System.out.println("|------------------------------------------------------|");
        System.out.printf("%-13s %-13s %-13s %-13s","|Board Size","|Min","|Max","|AVG");
        System.out.print("|");
        System.out.println();
        Collections.sort(this.statistic);
        //System.out.println(this.statistic.isEmpty());
        for(statOfDim row: this.statistic){
            System.out.printf("%-13s %-13s %-13s %-13s","|"+row.dim+"*"+row.dim,"|"+row.getMinTime()+" sec","|"+row.getMaxTime()+" sec","|"+row.getAVGtime()+" sec");
            System.out.print("|");
            System.out.println();
        }
        System.out.println("|------------------------------------------------------|");
        if(!this.errorList.isEmpty()){
            GenErrorTable();
        }
    }
    private void GenErrorTable(){
        String line,format;
        System.out.println("|------------------------------------------------------|");
        line="|Boards with invalid solution:";
        System.out.print(line);
        format="%"+(56-line.length())+"s";
        System.out.printf(format,"|");
        System.out.println();
        for(String fileName: this.errorList){
            line="|"+fileName;
            System.out.print(line);
            format="%"+(56-line.length())+"s";
            System.out.printf(format,"|");
            System.out.println();
        }
        System.out.println("|------------------------------------------------------|");
    }


}
