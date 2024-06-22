package cn.glfs.event;


public class UpdateRpcLister implements IRpcLister<UpdateRpcEventData> {
    @Override
    public void exec(UpdateRpcEventData updateRpcEventData) {
        System.out.println("触发修改件" + updateRpcEventData);

    }
}
