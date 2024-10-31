import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AgentInitializationException;
import java.io.IOException;

public class VirtualMachineExample {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("用法: java VirtualMachineExample <PID> <AgentJarPath>");
            return;
        }
        String pid = args[0];
        String agentJarPath = args[1];
        try {
            // 连接到目标JVM
            VirtualMachine vm = VirtualMachine.attach(pid);

            // 加载Agent
            vm.loadAgent(agentJarPath);

            // 分离
            vm.detach();
            System.out.println("Agent 加载成功");
        } catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException e) {
            e.printStackTrace();
        }
    }
}