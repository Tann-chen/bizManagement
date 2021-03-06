package com.user.controller;

import com.user.comm.exception.JsonParseException;
import com.user.comm.result.ModulePermissions;
import com.user.comm.result.Result;
import com.user.comm.result.ResultBuilder;
import com.user.domain.entity.Module;
import com.user.domain.entity.Permission;
import com.user.domain.entity.Role;
import com.user.service.ModuleService;
import com.user.service.RoleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping(value = "/role")
@CrossOrigin(origins = "http://localhost:8080")
public class RoleController {

    @Autowired
    private RoleInfoService roleInfoService;

    @Autowired
    private ModuleService moduleService;


    @RequestMapping(method = RequestMethod.POST)
    public Result addRole(@RequestBody Role role) throws Exception {
        if (null == role) {
            throw new JsonParseException("role");
        }
        roleInfoService.addRole(role);
        List<Role> roleList = roleInfoService.findAllRoles();

        return new ResultBuilder()
                .setCode(ResultBuilder.SUCCESS)
                .setData(roleList)
                .build();
    }


    @RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
    public Result getRoleDetails(@PathVariable Long roleId) throws Exception {
        if (null == roleId) {
            throw new IllegalArgumentException("roleId not empty");
        }
        Optional<Role> optionalRole = roleInfoService.getRoleById(roleId);

        ResultBuilder resultBuilder = new ResultBuilder();
        if (optionalRole.isPresent()) {
            resultBuilder.setData(ResultBuilder.SUCCESS).setData(optionalRole.get());
        } else {
            resultBuilder.setCode(ResultBuilder.SUCCESS).setMessage("role not existed");
        }

        return resultBuilder.build();
    }


    @RequestMapping(method = RequestMethod.GET)
    public Result getRoleList() {
        List<Role> roleList = roleInfoService.findAllRoles();
        List<Module> moduleList = moduleService.getAvailModulesByAdmin();

        Map<String, Object> result = new HashMap<>();
        result.put("role_list", roleList);
        result.put("module_list", moduleList);

        return new ResultBuilder()
                .setCode(ResultBuilder.SUCCESS)
                .setData(result)
                .build();
    }


    @RequestMapping(value = "/{roleId}", method = RequestMethod.PUT)
    public Result updateRoleDetails(@PathVariable Long roleId, @RequestBody Role role) throws Exception {
        if (null == roleId) {
            throw new IllegalArgumentException("roleId not empty");
        }
        if (null == role) {
            throw new JsonParseException("role");
        }
        Role updatedRole = roleInfoService.updateRole(roleId, role);

        return new ResultBuilder()
                .setCode(ResultBuilder.SUCCESS)
                .setData(updatedRole)
                .build();
    }


    @RequestMapping(value = "/{roleId}", method = RequestMethod.DELETE)
    public Result deleteRole(@PathVariable Long roleId) throws Exception {
        if (null == roleId) {
            throw new IllegalArgumentException("roleId not empty");
        }
        roleInfoService.changeIsAvailableStatus(roleId, false);
        List<Role> roleList = roleInfoService.findAllRoles();

        return new ResultBuilder()
                .setCode(ResultBuilder.SUCCESS)
                .setData(roleList)
                .build();
    }

    @RequestMapping(value = "/{roleId}/permissions", method = RequestMethod.GET)
    public Result getRolePermissions(@PathVariable Long roleId) {
        if (null == roleId) {
            throw new IllegalArgumentException("roleId not empty");
        }

        Map<String, ModulePermissions> roleModulePermissions = new HashMap<>();
        ResultBuilder resultBuilder = new ResultBuilder();

        List<Module> modules = moduleService.getAvailModulesByAdmin();
        Optional<Role> optionalRole = roleInfoService.getRoleById(roleId);

        if (!optionalRole.isPresent()) {
            resultBuilder.setCode(ResultBuilder.FAILED).setMessage("role not existed");
        } else {
            List<Permission> permissionList = optionalRole.get().getPermissionList();

            for (Module m : modules) {
                ModulePermissions mp = new ModulePermissions();

                for (Permission p : permissionList) {
                    if (m.equals(p.getModule())) {
                        mp.setFieldValue(p.getPermission().getType(), true);
                    }
                }

                roleModulePermissions.put(m.getName(), mp);
            }

            resultBuilder.setCode(ResultBuilder.SUCCESS).setData(roleModulePermissions);
        }

        return resultBuilder.build();
    }

}
