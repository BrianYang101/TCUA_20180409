#include <tc/tc.h>
#include <tcinit/tcinit.h>
#include <tc/tc_startup.h>
#include <tc/tc_macros.h>
#include <tc/emh.h>
#include <mld/journal/journal.h>
#include <tccore/aom.h>
#include <tc/folder.h>
#include <qry/qry.h>
#include <tccore/aom_prop.h>
#include <tccore/item.h>
#include <sa/tcfile.h>
#include <ss/ss_errors.h>
#include <tccore/grm.h>
#include <sa/tcfile.h>
#include <ae/ae.h>

#include <tccore/Item.hxx>
#include <tccore/ItemRevision.hxx>
#include <metaframework/CreateInput.hxx>
#include <metaframework/BusinessObjectRef.hxx>
#include <metaframework/BusinessObjectRegistry.hxx>
#include <base_utils/ScopedSmPtr.hxx>

#include <iostream>
#include <string>

using std::string;
using std::cout;
using std::endl;
using namespace Teamcenter;

#define CheckIfail(x)   \
{   \
    char *err_string = NULL;   \
    if((ifail = (x)) != ITK_ok)   \
    {    \
        EMH_ask_error_text (ifail, &err_string);   \
        TC_write_syslog("[ERROR] %d ERROR MSG: %s.\n", ifail, err_string);   \
        TC_write_syslog("[ERROR] Function: %s FILE: %s LINE: %d\n",#x, __FILE__, __LINE__);   \
        printf("[ERROR] %d ERROR MSG: %s.\n", ifail, err_string);   \
        printf("[ERROR] Function: %s FILE: %s LINE: %d\n",#x, __FILE__, __LINE__);   \
        if (err_string != NULL)   \
            MEM_free (err_string);   \
        goto EXIT;   \
    }   \
}

int queryBolt(char* name, int* boltCount, tag_t** bolts)
{
	int ifail = ITK_ok;
	std::vector<char*> critNames, critValues;

	tag_t queryTag	= NULLTAG;
	CheckIfail(QRY_find("Query C9Bolt", &queryTag));

	critNames.push_back("Name");
	critValues.push_back(name);

	CheckIfail(QRY_execute(queryTag, critNames.size(), critNames.data(), critValues.data(), boltCount, bolts));

EXIT:

	return ITK_ok;
}

int createBolt(const string& boltName, tag_t& boltTag)
{
	int ifail = ITK_ok;

    Teamcenter::CreateInput* itemCreateInput = 
		static_cast<CreateInput*>(BusinessObjectRegistry::instance().createInputObject("C9bolt", OPERATIONINPUT_CREATE));
	Teamcenter::CreateInput* revCreateInput = 
		static_cast<CreateInput*>(BusinessObjectRegistry::instance().createInputObject("C9boltRevision", OPERATIONINPUT_CREATE));

	ITKCALL(itemCreateInput->setString("object_name", boltName, false));
	//ITKCALL(itemCreateInput->setString("XXX", boltName, false));
	//if(ifail) return ifail;
	ITKCALL(itemCreateInput->setTag("revision", revCreateInput->getTag(), false));
	ITKCALL(revCreateInput->setDouble("c9diameter", 1.0, false));

    Teamcenter::Item* bolt = 
		dynamic_cast<Item*>(BusinessObjectRegistry::instance().createBusinessObject(itemCreateInput));
	ITKCALL(AOM_save_with_extensions(bolt->getTag()));
	ITKCALL(AOM_unlock(bolt->getTag()));
	boltTag = bolt->getTag();

EXIT:

	return ifail;
}

int updateBolt(tag_t boltRevTag)
{
	int ifail = ITK_ok;
	BusinessObjectRef<ItemRevision> boltRev(boltRevTag);

	ITKCALL(AOM_lock(boltRevTag));

	//CheckIfail(AOM_set_value_string(boltRevTag, "c9manufacturer", "Inventec"));

	ITKCALL(boltRev->setString("c9manufacturer", "ISOD", false));

	ITKCALL(AOM_save(boltRevTag));
	ITKCALL(AOM_unlock(boltRevTag));

EXIT:

	return ifail;
}

int createDataset(tag_t* dsTag)
{
	int ifail = ITK_ok;

	tag_t dsTypeTag, fileTag;
	IMF_file_t descriptor;

	CheckIfail(AE_find_datasettype("Text", &dsTypeTag));
	CheckIfail(AE_create_dataset_with_id(dsTypeTag, "My DS1", "My DS1", "id", "A", dsTag));
	CheckIfail(AE_set_dataset_format(*dsTag, "TEXT_REF"));
	CheckIfail(AOM_save(*dsTag));

	CheckIfail(IMF_fmsfile_import("c:\\temp\\1.txt", "1.txt", SS_TEXT , &fileTag, &descriptor));
	CheckIfail(AOM_refresh(*dsTag, TRUE));
	CheckIfail(AE_add_dataset_named_ref(*dsTag, "Text", AE_PART_OF, fileTag));
	CheckIfail(AOM_save(*dsTag));
	CheckIfail(AOM_unload(fileTag));
	CheckIfail(AOM_unload(*dsTag));

EXIT:

	return ifail;
}

int relateBolt(tag_t itemRevTag, tag_t dsTag)
{
	int ifail = ITK_ok;
	tag_t specTag = NULLTAG, relationTag = NULLTAG;

	CheckIfail(GRM_find_relation_type("IMAN_specification", &specTag));
	CheckIfail(GRM_create_relation(itemRevTag, dsTag, specTag, NULL, &relationTag));
	CheckIfail(GRM_save_relation(relationTag));

EXIT:

	return ifail;
}

int ITK_user_main(int argc, char* argv[])
{
	int ifail;
	char* text = nullptr;

	ITK_initialize_text_services (0);
	ifail = ITK_init_module("jgordon", "jgordon", "dba");
	if (ifail != ITK_ok)
	{
		EMH_ask_error_text(ifail, &text);
		printf("Error with ITK_init_module: %s \n", text);
		MEM_free(text);
		return ifail;
	}
	else printf("Logon to Temcenter succesfully.\n");

	/* Call your functions between here */
	int boltCount = 0;
	tag_t* bolts = nullptr;
	//scoped_smptr<tag_t> bolts;

	//CheckIfail(queryBolt("*", &boltCount, &bolts));
	CheckIfail(queryBolt("*", &boltCount, &bolts));
	cout << "boltCount = " << boltCount << endl;
	for(int ii=0; ii<boltCount; ii++) {
		/*
		char* id = nullptr;
		CheckIfail(AOM_ask_value_string(bolts[ii], "item_id", &id));
		cout << "bolt[" << ii << "] = " << id << endl;
		MEM_free(id);
		*/
		BusinessObjectRef<Item> bolt(bolts[ii]); 
		string id;
		bool isNull;
		bolt->getItem_id(id, isNull);
		cout << "bolt[" << ii << "] = " << id << endl;
	}

	/*
	tag_t boltTag = NULLTAG;
	CheckIfail(createBolt("B1", boltTag));
	ITKCALL(FL_user_update_newstuff_folder(boltTag));
	*/

	for(int ii=0; ii<boltCount; ii++) {
		tag_t boltRevTag = NULLTAG;
		CheckIfail(ITEM_ask_latest_rev(bolts[ii], &boltRevTag));
		CheckIfail(updateBolt(boltRevTag));
	}

	/*
	tag_t dsTag = NULLTAG;
	createDataset(&dsTag);
	CheckIfail(FL_user_update_newstuff_folder(dsTag));
	for(int ii=0; ii<boltCount; ii++) {
		tag_t boltRevTag = NULLTAG;
		CheckIfail(ITEM_ask_latest_rev(bolts[ii], &boltRevTag));
		CheckIfail(relateBolt(boltRevTag, dsTag));
	}
	*/

EXIT:

	MEM_free(bolts);
	ITK_exit_module( TRUE);
	return ifail;
}