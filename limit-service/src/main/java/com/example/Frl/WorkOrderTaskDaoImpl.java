package com.example.Frl;

//@Service
//@Slf4j
public class WorkOrderTaskDaoImpl implements WorkOrderTaskDao {

    @Override
    public void saveWorkOrderTask(WorkOrderTask workOrderTask) {
        HBUtil.save(workOrderTask);
    }

    @Override
    public WorkOrderTask findWorkOrderTaskById(int workOrderTaskId) {
        return HBUtil.find(WorkOrderTask.class, workOrderTaskId);
    }

    @Override
    public void deleteWorkOrderTask(WorkOrderTask workOrderTask) {
        HBUtil.delete(workOrderTask);
    }

    @Override
    public List<WorkOrderTask> findWorkOrderTasksByWorkOrderIdAndModuleId(int workOrderId, int moduleId) {
        return findWorkOrderTasksByWorkOrderIdAndModuleId(workOrderId, Collections.singletonList(moduleId));
    }

    @Override
    public List<WorkOrderTask> findWorkOrderTasksByWorkOrderIdAndModuleId(int workOrderId, List<Integer> moduleIds) {
        List<WorkOrderTask> workOrderTasks = new ArrayList<>();
        int moduleId = moduleIds.get(0);
        try {
            String sql =
                    " SELECT " +
                    "   wt " +
                    " FROM " +
                    "   WorkOrderTask wt " +
                    " WHERE " +
                    "   wt.woId = :woId " +
                    (moduleId == -1 ? " AND wt.checklistCategoryId is null " : "   AND wt.checklistCategoryId IN (:checklistCategoryIds) ") +
                    " ORDER BY " +
                    "   wt.routerSeq ";

            Object[] params;
            if (moduleId == -1) {
                params = new Object[]{"woId", workOrderId};
            } else {
                params = new Object[]{"woId", workOrderId, "checklistCategoryIds", moduleIds};
            }

            workOrderTasks = HBUtil.getList(
                    sql,
                    params,
                    WorkOrderTask.class);

        } catch (Exception e) {
            log.error("Failed to load work order tasks of woId " + workOrderId + " and moduleId " + moduleId, e);
        }
        return workOrderTasks;
    }

    @Override
    public int findNumberOfWorkOrderTasksByWorkOrderId(int workOrderId) {
        Long count = 0L;
        try {
            String sql =
                    " SELECT " +
                            "   count(distinct wt) " +
                            " FROM " +
                            "   WorkOrderTask as wt " +
                            " WHERE " +
                            "   wt.woId = :woId ";
            count = HBUtil.getUniqueResult(
                    sql,
                    new Object[]{"woId", workOrderId},
                    Long.class);

        } catch (Exception e) {
            log.error("Failed to count work order tasks of woId " + workOrderId, e);
        }
        return count != null ? count.intValue() : 0;
    }

    @Override
    public List<WorkOrderTask> findWorkOrderTasksByWorkOrderId(int workOrderId) {
        List<WorkOrderTask> workOrderTasks = new ArrayList<>();
        try {
            String sql =
                    " SELECT " +
                            "   wt " +
                            " FROM " +
                            "   WorkOrderTask wt " +
                            " WHERE " +
                            "   wt.woId = :woId " +
                            "   AND wt.checklistCategoryId IS NOT NULL " +
                            " ORDER BY " +
                            "   wt.routerSeq ";
            workOrderTasks = HBUtil.getList(
                    sql,
                    new Object[]{"woId", workOrderId},
                    WorkOrderTask.class);
        } catch (Exception e) {
            log.error("Failed to load work order tasks by Work Order Id: " + workOrderId, e);
        }
        return workOrderTasks;
    }

    @Override
    public int findNumberOfPrelimWorkOrderTasksByWorkOrderId(int workOrderId) {
        Long count = 0L;
        try {
            String sql =
                    " SELECT " +
                    "   count(distinct wt) " +
                    " FROM " +
                    "   WorkOrderTask wt " +
                    " WHERE " +
                    "   wt.woId = :woId " +
                    "   AND wt.isPrelimPhoto is true ";
            count = HBUtil.getUniqueResult(
                    sql,
                    new Object[]{"woId", workOrderId},
                    Long.class);
        } catch (Exception e) {
            log.error("Failed to load work order tasks by Work Order Id: " + workOrderId, e);
        }
        return count != null ? count.intValue() : 0;
    }

    @Override
    public List<WorkOrderTask> findAllWorkOrderTasksByWorkOrderId(int workOrderId) {
        List<WorkOrderTask> workOrderTasks = new ArrayList<>();
        try {
            String sql =
                    " SELECT " +
                            "   wt " +
                            " FROM " +
                            "   WorkOrderTask wt " +
                            " WHERE " +
                            "   wt.woId = :woId " +
                            " ORDER BY " +
                            "    -wt.routerSeq desc ";
            workOrderTasks = HBUtil.getList(
                    sql,
                    new Object[]{"woId", workOrderId},
                    WorkOrderTask.class);
        } catch (Exception e) {
            log.error("Failed to load all work order tasks by Work Order Id: " + workOrderId, e);
        }
        return workOrderTasks;
    }

    @Override
    public WorkOrderTask findWorkOrderTaskByWorkOrderIdAndChecklistTaskId(int workOrderId, int checklistTaskId) {
        WorkOrderTask workOrderTask = null;
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId AND wt.checklistTaskId = :checklistTaskId";
        List<WorkOrderTask> workOrderTasks = HBUtil.getList(query, new Object[]{"woId", workOrderId, "checklistTaskId", checklistTaskId}, WorkOrderTask.class);
        if(workOrderTasks.size() > 0){
            workOrderTask = workOrderTasks.get(0);
        }
        return workOrderTask;
    }

    @Override
    public WorkOrderTask findWorkOrderTaskByWorkOrderIdAndChecklistTaskIdAndComponentId(int workOrderId, int checklistTaskId, int checklistComponentId ) {
        WorkOrderTask workOrderTask = null;
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND  wt.checklistComponentId = :checklistComponentId " +
                (checklistTaskId == 0 ? " AND wt.checklistTaskId is null " : "  AND wt.checklistTaskId = :checklistTaskId ");
        Object[] params;
        if (checklistTaskId == 0) {
            params = new Object[]{"woId", workOrderId, "checklistComponentId" , checklistComponentId, };
        } else {
            params = new Object[]{"woId", workOrderId, "checklistComponentId" , checklistComponentId, "checklistTaskId", checklistTaskId};
        }
        List<WorkOrderTask> workOrderTasks = HBUtil.getList(query, params, WorkOrderTask.class);
        if(workOrderTasks.size() > 0){
            workOrderTask = workOrderTasks.get(0);
        }
        return workOrderTask;
    }

    @Override
    public List<WorkOrderTask> findWorkOrderTaskByWorkOrderIdAndCategoryName(int workOrderId, String categoryName) {
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND  wt.category = :category ";
        Object[] params =  new Object[]{"woId", workOrderId, "category" , categoryName};
        return HBUtil.getList(query, params, WorkOrderTask.class);
    }

    @Override
    public List<WorkOrderTask> findWorkOrderTasksByWorkOrderIdAndOperation(int workOrderId, String operation) {
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND  wt.operation = :operation ";
        Object[] params =  new Object[]{"woId", workOrderId, "operation" , operation};
        return HBUtil.getList(query, params, WorkOrderTask.class);
    }

    @Override
    public List<WorkOrderTask> findHotspotWorkOrderTasksByWorkOrderId(int workOrderId) {
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND  wt.hotspotName IS NOT NULL ";
        Object[] params =  new Object[]{"woId", workOrderId};
        return HBUtil.getList(query, params, WorkOrderTask.class);
    }

    @Override
    public void deleteWorkOrderTasksByIds(List<Integer> workOrderTaskIds) {
        String query = "delete FROM WorkOrderTask WHERE woTaskId IN (:workOrderTaskIds) ";
        HBUtil.executeUpdate(query, new Object[]{"workOrderTaskIds", workOrderTaskIds});
    }

    @Override
    public void saveWorkOrderTasks(List<WorkOrderTask> beingUpdatedWorkOrderTasks) {
        HBUtil.saveOrUpdateBatch(beingUpdatedWorkOrderTasks);
    }

    @Override
    public void deleteWorkOrderTask(int woId) {
        String query = "delete FROM WorkOrderTask WHERE woId = :woId AND imageFile is null " +
                       " and rating is null AND inspectionComment is null and videoFile is null ";
        HBUtil.executeUpdate(query, new Object[]{"woId", woId});
    }

    @Override
    public void updateAllInspectionTaskPhotoUploadedTime(int woId) {
        String query = "update WorkOrderTask wt set wt.photoUploadedDate = now() where " +
                "wt.photoUploadedDate is null " +
                "and wt.woId = :woId " +
                "and wt.imageFile is not null";
        HBUtil.executeUpdate(query, new Object[]{"woId", woId});
    }

    @Override
    public void updateInspectionTaskPhotoUploadedTime(int woId, String photoFileName) {
        String query = "update WorkOrderTask wt set wt.photoUploadedDate = now() where wt.woId = :woId and wt.imageFile like :photoFileName";
        HBUtil.executeUpdate(query, new Object[]{"woId", woId, "photoFileName",  photoFileName + "%"});
    }

    @Override
    public void deleteWorkOrderTaskForWoId(int woId) {
        String query = "delete FROM WorkOrderTask WHERE woId = :woId ";
        HBUtil.executeUpdate(query, new Object[]{"woId", woId});
    }

    @Override
    public WorkOrderTask findWorkOrderTaskByWorkOrderIdAndCategoryIdAndTaskName(int workOrderId, String taskName, int checklistCategoryId) {
        WorkOrderTask workOrderTask = null;
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND  wt.operation = :operation and wt.checklistCategoryId = :checklistCategoryId ";
        Object[] params = new Object[]{"woId", workOrderId, "operation", taskName, "checklistCategoryId", checklistCategoryId};
        List<WorkOrderTask> tasks =  HBUtil.getList(query, params, WorkOrderTask.class);
        if(tasks.size() >0){
            workOrderTask =  tasks.get(0);
        }
        return workOrderTask;
    }

    @Override
    public List<WorkOrderTask> getAllMediaTasks(int workOrderId) {
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND (wt.imageFile is not null or wt.videoFile is not null)";
        Object[] params = new Object[]{"woId", workOrderId};
        return HBUtil.getList(query, params, WorkOrderTask.class);
    }

    @Override
    public List<WorkOrderTaskInfo> getWorkOrderTaskInfosFromInspectionReport(int woId, List<Integer> checklistCategoryIds, String moduleName) {
        List<WorkOrderTaskInfo> workOrderTaskInfos = new ArrayList<>();
        int checklistCategoryId = checklistCategoryIds.get(0);
        try {
            String sql =
                    " SELECT " +
                    "   wt " +
                    " FROM " +
                    "   WorkOrderTask wt " +
                    " WHERE " +
                    "   wt.woId = :woId " +
                    (checklistCategoryId == -1 ? (moduleName != null ? " AND wt.category = :moduleName " : " ") : "   AND wt.checklistCategoryId IN (:checklistCategoryIds) ") +
                    " ORDER BY " +
                    "   wt.routerSeq ";
            Object[] params;
            if (checklistCategoryId == -1) {
                params = moduleName != null ? new Object[]{"woId", woId, "moduleName", moduleName} : new Object[]{"woId", woId};
            } else {
                params = new Object[]{"woId", woId, "checklistCategoryIds", checklistCategoryIds};
            }
            List<WorkOrderTask> results = HBUtil.getList(
                    sql,
                    params,
                    WorkOrderTask.class);

            for (WorkOrderTask workOrderTask : results) {
                workOrderTaskInfos.add(new WorkOrderTaskInfo(workOrderTask));
            }
        } catch (Exception e) {
            log.error("Failed to load inspection data of woId " + woId + " checklistCategoryId " + checklistCategoryId, e);
        }
        return workOrderTaskInfos;
    }

    @Override
    public List<WorkOrderTaskInfo> getWorkOrderTaskInfosByWoId(int woId) {
        List<WorkOrderTaskInfo> workOrderTaskInfos = new ArrayList<>();
        try {
            String sql =
                    " SELECT " +
                            "   wt " +
                            " FROM " +
                            "   WorkOrderTask wt " +
                            " WHERE " +
                            "   wt.woId = :woId " +
                            " ORDER BY " +
                            "   wt.routerSeq ";
            List<WorkOrderTask> results = HBUtil.getList(
                    sql,
                    new Object[]{"woId", woId},
                    WorkOrderTask.class);

            for (WorkOrderTask workOrderTask : results) {
                workOrderTaskInfos.add(new WorkOrderTaskInfo(workOrderTask));
            }
        } catch (Exception e) {
            log.error("Failed to load work order task info data of woId " + woId, e);
        }
        return workOrderTaskInfos;
    }

    @Override
    public WorkOrderTask findWorkOrderTaskByVideoId(int workOrderId, String videoId) {
        WorkOrderTask workOrderTask = null;
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND  wt.videoId = :videoId ";
        Object[] params = new Object[]{"woId", workOrderId, "videoId", videoId};
        List<WorkOrderTask> tasks =  HBUtil.getList(query, params, WorkOrderTask.class);
        if(tasks.size() > 0) {
            workOrderTask =  tasks.get(0);
        }
        return workOrderTask;
    }

    @Override
    public List<WorkOrderTask> findSpinWorkOrderTasksUsingWoId(List<Integer> woIds) {
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId in :woIds AND wt.internalSpinId is not null";
        Object[] params = new Object[]{"woIds", woIds};
        return HBUtil.getList(query, params, WorkOrderTask.class);
    }

    @Override
    public WorkOrderTask findWorkOrderTaskByWorkOrderIdAndChecklistTaskIdAndCategoryId(int workOrderId, int checklistTaskId, int checklistCategoryId) {
        WorkOrderTask workOrderTask = null;
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId  AND  wt.checklistCategoryId = :checklistCategoryId " +
                (checklistTaskId == 0 ? " AND wt.checklistTaskId is null " : "  AND wt.checklistTaskId = :checklistTaskId ");
        Object[] params;
        if (checklistTaskId == 0) {
            params = new Object[]{"woId", workOrderId, "checklistCategoryId" , checklistCategoryId };
        } else {
            params = new Object[]{"woId", workOrderId, "checklistCategoryId" , checklistCategoryId, "checklistTaskId", checklistTaskId};
        }
        List<WorkOrderTask> workOrderTasks = HBUtil.getList(query, params, WorkOrderTask.class);
        if(workOrderTasks.size() > 0){
            workOrderTask = workOrderTasks.get(0);
        }
        return workOrderTask;
    }

    @Override
    public List<WorkOrderTask> findBrochurePhotoWorkOrderTasks(int workOrderId) {
        String query = "SELECT wt FROM WorkOrderTask wt WHERE wt.woId = :woId AND upper(wt.operation) = :brochureTaskName";
        Object[] params = new Object[]{"woId", workOrderId, "brochureTaskName", BrochurePhoto.taskName.toUpperCase()};
        return HBUtil.getList(query, params, WorkOrderTask.class);
    }

	
}
