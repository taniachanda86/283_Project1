package cmpe283.project1;

import java.net.URL;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class SnapShotMgr {

	public static void createSanpshot(VM vm) throws Exception {
		if (!Ping.pingIP(vm.getIP())) {
			System.out.println("Cannot ping " + vm.getName() + ". Snapshot skipped.");
			return;
		}
		System.out.println("Ping success. Creating snapshot for " + vm.getName() + "......");
		String snapshotname = vm.getName() + "-SnapShot";
		String desc = "A snapshot of " + vm.getName();

		Task task = vm.getVM().createSnapshot_Task(snapshotname, desc, false, false);
		if (task.waitForTask() == Task.SUCCESS)
			System.out.println(snapshotname + " was created.");
		else
			System.out.println(snapshotname + " create failure.");
	}

	public static void createSanpshot(VHost vhost) throws Exception {
		ServiceInstance superVCenter = new ServiceInstance(new URL(Setting.SuperVcenterUrl),
				Setting.UserName, Setting.Password, true);
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(superVCenter.getRootFolder())
		.searchManagedEntity("VirtualMachine", Setting.VMs.get(vhost.getIP()));
		
		if (Ping.pingIP(vhost.getIP())) {
			System.out.println("Ping success. Creating snapshot for " + vm.getName() + "......");
			String snapshotname = vm.getName() + "-SnapShot";
			String desc = "A snapshot of " + vm.getName();
			

			Task task = vm.createSnapshot_Task(snapshotname, desc, false, false);
			if (task.waitForTask() == Task.SUCCESS)
				System.out.println(snapshotname + " was created.");
			else
				System.out.println(snapshotname + " create failure.");
		} else {
			System.out.println("Cannot ping " + vm.getName() + ". Snapshot skipped.");
		}
		
		superVCenter.getServerConnection().logout();
	}
	
	public static boolean revert2LastSnapshot(VM vm) throws Exception {
		Task task = vm.getVM().revertToCurrentSnapshot_Task(null);
		if (task.waitForTask() == Task.SUCCESS) {
			System.out.println(vm.getName() + " was reverted to last snapshot.");
			return true;
		} else {
			System.out.println(vm.getName() + " recover failure.");
			return false;
		}
	}
	
	public static boolean revert2LastSnapshot(VHost vhost) throws Exception {
		ServiceInstance superVCenter = new ServiceInstance(new URL(Setting.SuperVcenterUrl),
				Setting.UserName, Setting.Password, true);
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(superVCenter.getRootFolder())
		.searchManagedEntity("VirtualMachine", Setting.VMs.get(vhost.getIP()));
		VM v = new VM(vm); 
		boolean res = revert2LastSnapshot(v);
		v.powerOn();
		
		superVCenter.getServerConnection().logout();
		return res;
	}
	
	
	public static void removeAllSnapshot(VM vm) throws Exception {
		Task task = vm.getVM().removeAllSnapshots_Task();
		if (task.waitForTask() == Task.SUCCESS) {
			System.out.println("Removed all snapshots for " + vm.getName());
		}
	}

	
}
