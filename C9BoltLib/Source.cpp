#include <stdlib.h>
#include <stdarg.h>

#include <ug_va_copy.h>
#include <tccore/aom.h>
#include <tccore/custom.h>
#include <tc/emh.h>
#include <ict/ict_userservice.h>
#include <tc/tc.h>
#include <tccore/tctype.h>
#include <sa/tcfile.h>
#include <itk/mem.h>
#include <ss/ss_errors.h>
#include <sa/user.h>
#include <tcinit/tcinit.h>
#include <tc/emh.h>
#include <mld/journal/journal.h>
#include <epm/epm.h>
#include <tccore/aom_prop.h>
#include <ae/dataset.h>

#include <vector>
#include <string>

using std::string;
using std::vector;

int C9TestActionHandler( EPM_action_message_t msg )
{
	int ifail = ITK_ok;
	tag_t userTag = NULLTAG, rootTask = NULLTAG;
	char* userName = nullptr;
	int iNumAttch = 0;
	tag_t* pTagAttch = nullptr;
	
	printf("C9TestActionHandler\n");

	ITKCALL(POM_get_user(&userName, &userTag));

	printf("C9TestActionHandler userName = %s\n", userName);

	int iArgs = TC_number_of_arguments(msg.arguments);
	for(int ii=0; ii<iArgs; ii++) {
		char *arg = TC_next_argument(msg.arguments);
		char* flag = nullptr;
		char* value = nullptr;
		ITKCALL(ITK_ask_argument_named_value(arg,  &flag, &value));
		printf("C9TestActionHandler argument[%d]: flag=%s, value=%s\n", ii, flag, value);

		MEM_free(flag);
		MEM_free(value);
	}

	ITKCALL(EPM_ask_root_task(msg.task, &rootTask));
	ITKCALL(EPM_ask_attachments(rootTask, EPM_target_attachment, &iNumAttch, &pTagAttch));

	//Sleep(10000);
	for(int ii=0; ii<iNumAttch; ii++) {
		char* name = nullptr;
		ITKCALL(AOM_get_value_string(pTagAttch[ii], "object_name", &name));
		printf("C9TestActionHandler attach[%d] = %s\n", ii, name);
	}

	MEM_free(userName);
	MEM_free(pTagAttch);

    return ifail;
}

EPM_decision_t C9TestRuleHandler(EPM_rule_message_t msg)
{
	EPM_decision_t decision = EPM_go;
	//EPM_decision_t decision = EPM_nogo;
	int ifail;

	printf("C9TestRuleHandler\n");

	return decision;
} 

int C9BoltLib_init_module(int *decision, va_list args)
{
	int ifail = ITK_ok;

	printf("\n C9BoltLib_init_module \n");

	ITKCALL(EPM_register_rule_handler("C9TestRuleHandler", "", C9TestRuleHandler)); 
	ITKCALL(EPM_register_action_handler("C9TestActionHandler", "", C9TestActionHandler)); 

	return ifail;
}

int C9QueryDatasets(char* name, int* count, tag_t** tags)
{
	int ifail = ITK_ok;
	std::vector<char*> critNames, critValues;

	tag_t queryTag	= NULLTAG;
	ITKCALL(QRY_find("Dataset Name", &queryTag));

	critNames.push_back("Dataset Name");
	critValues.push_back("My*");

	ITKCALL(QRY_execute(queryTag, critNames.size(), critNames.data(), critValues.data(), count, tags));

EXIT:

	return ITK_ok;
}

int C9QueryMyUsers(int* count, tag_t** users)
{
	int ifail = ITK_ok;

	std::vector<char*> critNames, critValues;

	tag_t queryTag	= NULLTAG;
	ITKCALL(QRY_find("Query Users", &queryTag));

	critNames.push_back("Name");
	critValues.push_back("*");

	ITKCALL(QRY_execute(queryTag, critNames.size(), critNames.data(), critValues.data(), count, users));

	return ifail;
}

int C9boltRevisionMyUsersGetter(METHOD_message_t* m, va_list args)
{
	printf("\n\n C9boltRevisionMyUsersGetter \n\n");

    int ifail = ITK_ok;
    va_list largs;
    va_copy(largs, args);
    tag_t propTag = va_arg(largs, tag_t);
	int* count = va_arg(largs, int*);
    tag_t** tags =  va_arg(largs, tag_t**); 
    va_end(largs);
    
    *count = 0;
    *tags = nullptr;

	tag_t boltRevTag = NULLTAG;
    METHOD_PROP_MESSAGE_OBJECT(m, boltRevTag);
	
	ITKCALL(C9QueryMyUsers(count, tags));
	//printf("count = %d", *count);
	//MEM_free(tags);

    return ifail;
}

int C9boltRevision_IMAN_save_PostAction(METHOD_message_t*  msg, va_list args)
{
	int ifail = ITK_ok;

	printf("C9boltRevision_IMAN_save_PostAction\n");
	tag_t boltRevTag = va_arg( args,tag_t);

	printf("boltRevTag = %d\n", boltRevTag);
	char* id = nullptr;
	ITKCALL(AOM_ask_value_string(boltRevTag, "item_id", &id));
	printf("id = %s\n", id);
	ITKCALL(AOM_set_value_string(boltRevTag, "c9manufacturer", "IMAN_save"));
	ITKCALL(AOM_save(boltRevTag)); 

    return ifail;
}

int C9BoltLib_user_init_module(int *decision, va_list args)
{
	int ifail = ITK_ok;
	METHOD_id_t  method;
	TC_argument_list_t *user_args = NULL;

	printf("\n C9BoltLib_user_init_module \n");

	ITKCALL(METHOD__register_prop_operationFn( "C9boltRevision", "c9MyUsers", PROP_ask_value_tags_msg, &C9boltRevisionMyUsersGetter, 0, &method));
	ITKCALL(METHOD_find_method("ImanRelation", "IMAN_save", &method));
	ITKCALL(METHOD_add_action(method, METHOD_post_action_type, (METHOD_function_t)C9boltRevision_IMAN_save_PostAction, user_args));

	return ifail;
}

#ifdef __cplusplus
extern "C" {
#endif

extern DLLAPI int C9BoltLib_register_callbacks()
{
    printf("\n C9BoltLib_register_callbacks \n");

	ITKCALL(CUSTOM_register_exit("C9BoltLib", "USER_gs_shell_init_module", (CUSTOM_EXIT_ftn_t)C9BoltLib_init_module));
    ITKCALL(CUSTOM_register_exit("C9BoltLib", "USER_init_module", (CUSTOM_EXIT_ftn_t)C9BoltLib_user_init_module));

    return ITK_ok;
}

#ifdef __cplusplus
}
#endif 