package fund.mymutual.cfsws.controller;

import fund.mymutual.cfsws.databean.CFSRole;

public class CFSForbiddenException extends Exception {
    private static final long serialVersionUID = 1L;
    private CFSRole roleRequired;

    public CFSForbiddenException(CFSRole roleRequired) {
        this.roleRequired = roleRequired;
    }

    public CFSRole getRoleRequired() {
        return roleRequired;
    }
}
