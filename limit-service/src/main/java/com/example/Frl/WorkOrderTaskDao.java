package com.example.Frl;
public interface WorkOrderTaskDao {

    void saveWorkOrderTask(WorkOrderTask workOrderTask);
    WorkOrderTask findWorkOrderTaskById(int workOrderTaskId);
    void deleteWorkOrderTask(WorkOrderTask workOrderTask);
    List<WorkOrderTask> findWorkOrderTasksByWorkOrderIdAndModuleId(int workOrderId, int moduleId);
    List<WorkOrderTask> findWorkOrderTasksByWorkOrderIdAndModuleId(int workOrderId, List<Integer> moduleIds);
    int findNumberOfWorkOrderTasksByWorkOrderId(int workOrderId);
    List<WorkOrderTask> findWorkOrderTasksByWorkOrderId(int workOrderId);
    int findNumberOfPrelimWorkOrderTasksByWorkOrderId(int workOrderId);
    List<WorkOrderTask> findAllWorkOrderTasksByWorkOrderId(int workOrderId);
    WorkOrderTask findWorkOrderTaskByWorkOrderIdAndChecklistTaskId(int workOrderId, int checklistTaskId);
    WorkOrderTask findWorkOrderTaskByWorkOrderIdAndChecklistTaskIdAndComponentId(int workOrderId, int checklistTaskId, int checklistComponentId);
    List<WorkOrderTask> findWorkOrderTaskByWorkOrderIdAndCategoryName(int workOrderId, String categoryName);
    void saveWorkOrderTasks(List<WorkOrderTask> beingUpdatedWorkOrderTasks);
    void deleteWorkOrderTask(int woId);
    void updateAllInspectionTaskPhotoUploadedTime(int woId);
    void updateInspectionTaskPhotoUploadedTime(int woId, String photoFileName);
    void deleteWorkOrderTaskForWoId(int woId);
    WorkOrderTask findWorkOrderTaskByWorkOrderIdAndCategoryIdAndTaskName(int workOrderId, String taskName, int checklistCategoryId);
    List<WorkOrderTask> getAllMediaTasks(int workOrderId);
    List<WorkOrderTaskInfo> getWorkOrderTaskInfosFromInspectionReport(int woId, List<Integer> checklistCategoryIds, String moduleName);
    List<WorkOrderTaskInfo> getWorkOrderTaskInfosByWoId(int woId);
    WorkOrderTask findWorkOrderTaskByVideoId(int workOrderId, String videoId);
    WorkOrderTask findWorkOrderTaskByWorkOrderIdAndChecklistTaskIdAndCategoryId(int workOrderId, int checklistTaskId, int checklistCategoryId);
    List<WorkOrderTask> findSpinWorkOrderTasksUsingWoId(List<Integer> woIds);
    List<WorkOrderTask>  findBrochurePhotoWorkOrderTasks(int workOrderId);
    List<WorkOrderTask> findWorkOrderTasksByWorkOrderIdAndOperation(int workOrderId, String operation);
    void deleteWorkOrderTasksByIds(List<Integer> workOrderTaskIds);
    List<WorkOrderTask> findHotspotWorkOrderTasksByWorkOrderId(int workOrderId);
}
