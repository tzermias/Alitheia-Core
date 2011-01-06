package eu.sqoooss.admin.test;

import eu.sqooss.admin.AdminActionBase;

public class SucceedingAction extends AdminActionBase {

    @Override
    public String mnemonic() {
        return "win";
    }

    @Override
    public String descr() {
        return "An action that enjoys success";
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        result("1", "#win");
        finished();
    }
}