package com.example.classrep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Fund;
import com.example.classrep.database.entity.FundChronology;
import com.example.classrep.database.entity.Institute;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;

import java.util.Date;
import java.util.List;

@Dao
public interface ClassRepDAO {

    //Query per entità Institute

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertInstitute(Institute...institutes);

    @Query("SELECT * FROM institute")
    List<Institute> getAllInstitute();

    @Query("SELECT * FROM institute WHERE id_institute = :id")
    Institute getInstitute(int id);

    @Query("DELETE FROM institute WHERE id_institute in (:id)")
    void deleteInstitute(List<Integer> id);

    @Query("SELECT MAX(id_institute) FROM institute")
    int getMaxIdInstitute();



    //Query per entità Adhesion

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAdhesion(Adhesion...adhesions);

    @Query("SELECT * FROM adhesion WHERE foreign_event = :id")
    List<Adhesion> getEventAdhesion(int id);



    //Query per entità Child

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertChild(Child...children);

    @Query("SELECT * FROM child WHERE foreign_event = :id")
    List<Child> getEventChildren(int id);



    //Query per entità Event

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEvent(Event...events);

    @Query("SELECT * FROM event WHERE foreign_institute = :id")
    List<Event> getAllEvent(int id);

    @Query("SELECT * FROM event WHERE children = :child AND adhesions = :adhesion")
    List<Event> getFilteredEvent(boolean child, boolean adhesion);



    //Query per entità Fund

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFund(Fund...funds);

    @Query("SELECT * FROM fund WHERE foreign_institute = :id")
    Fund getFund(int id);



    //Query per entità FundChronology

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFundChronology(FundChronology...fundChronologies);

    @Query("SELECT * FROM fund_chronology WHERE foreign_fund = :id")
    List<FundChronology> getAllFundChronology(int id);

    @Query("SELECT * FROM fund_chronology WHERE foreign_fund = :id AND date BETWEEN :start_date AND :finish_date")
    @TypeConverters({DataConverter.class})
    List<FundChronology> getFilteredDateFund(int id, Date start_date, Date finish_date);

    @Query("SELECT * FROM fund_chronology WHERE foreign_fund = :id AND `action` = :type")
    @TypeConverters({DataConverter.class})
    List<FundChronology> getFilteredTypeFund(int id, String type);

    @Query("SELECT * FROM fund_chronology WHERE foreign_fund = :id AND `action` = :type AND date BETWEEN :start_date AND :finish_date")
    @TypeConverters({DataConverter.class})
    List<FundChronology> getFilteredFund(int id, String type, Date start_date, Date finish_date);


    //Query per entità Meeting

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMeeting(Meeting...Meeting);

    @Query("SELECT * FROM meeting WHERE foreign_institute = :id")
    List<Meeting> getAllMeeting(int id);

    @Query("UPDATE meeting SET report = :text")
    void updateReport(String text);



    //Query per entità Parent

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertParent(Parent...parent);

    @Query("SELECT * FROM parent WHERE foreign_event = :id")
    List<Parent> getEventParents(int id);

    @Query("SELECT * FROM parent WHERE foreign_pta = :id")
    List<Parent> getPTAmeetingParents(int id);



    //Query per entità PTAmeeting

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPTAmeeting(PTAmeeting...ptameetings);

    @Query("SELECT * FROM pta_meeting WHERE foreign_institute = :id")
    List<PTAmeeting> getAllPTAmeeting(int id);

}
