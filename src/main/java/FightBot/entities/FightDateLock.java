package FightBot.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FightDateLock {
    private LocalDate date;
    private long firstFighterId;
    private long secondFighterID;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FightDateLock)) {
            return false;
        }

        FightDateLock other = (FightDateLock) o;
        if (!other.getDate().equals(this.date)) {
            return false;
        }

//        "date" : [ 2021, 11, 18 ],
//        "firstFighterId" : 329388314333282304,
//        "secondFighterID" : 906693294866694146
//
//        "date" : [ 2021, 11, 18 ],
//        "firstFighterId" : 217576948195524608,
//        "secondFighterID" : 329388314333282304

        if (other.getFirstFighterId() == this.firstFighterId || other.getFirstFighterId() == this.secondFighterID) {
            if (other.getSecondFighterID() == this.firstFighterId || other.getSecondFighterID() == this.secondFighterID) {
                return true;
            }
        }

        if (other.getSecondFighterID() == this.firstFighterId || other.getSecondFighterID() == this.secondFighterID) {
            if (other.getFirstFighterId() == this.firstFighterId || other.getFirstFighterId() == this.secondFighterID) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this.firstFighterId + ", " + this.secondFighterID + ", " + this.date + "]";
    }
}
