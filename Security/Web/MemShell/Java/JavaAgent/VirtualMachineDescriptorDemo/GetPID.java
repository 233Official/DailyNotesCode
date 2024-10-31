import com.sun.tools.attach.VirtualMachine;  
import com.sun.tools.attach.VirtualMachineDescriptor;  

import java.util.List;  

public class GetPID {  
    public static void main(String[] args) {  
        if (args.length != 1) {  
            System.out.println("请提供目标 JVM 名称作为参数。");  
            return;  
        }  

        String targetName = args[0];  
        List<VirtualMachineDescriptor> list = VirtualMachine.list();  
        boolean found = false;

        for (VirtualMachineDescriptor vmd : list){  
            if(vmd.displayName().equals(targetName)) {  
                System.out.println("PID: " + vmd.id());  
                found = true;
                break;
            }  
        }  

        if (!found) {
            System.out.println("未找到名称为 " + targetName + " 的 JVM。");
        }
    }  
}